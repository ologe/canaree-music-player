package dev.olog.data.spotify.gateway

import androidx.work.*
import dev.olog.data.spotify.db.GeneratedPlaylistsDao
import dev.olog.data.spotify.db.SpotifyImageEntity
import dev.olog.data.spotify.db.SpotifyImagesDao
import dev.olog.data.spotify.dto.RemoteSpotifyAlbum
import dev.olog.data.spotify.dto.RemoteSpotifyArtist
import dev.olog.data.spotify.extensions.getInt
import dev.olog.data.spotify.extensions.getWorkInfoAsFlow
import dev.olog.data.spotify.mapper.toDomain
import dev.olog.data.spotify.mapper.toPlaylist
import dev.olog.data.spotify.service.SpotifyService
import dev.olog.data.spotify.workers.SpotifyTrackAudioFeatureFetcherWorker
import dev.olog.data.spotify.workers.SpotifyTrackFetcherWorker
import dev.olog.domain.MediaId
import dev.olog.domain.entity.spotify.SpotifyAlbum
import dev.olog.domain.entity.spotify.SpotifyAlbumType
import dev.olog.domain.entity.spotify.SpotifyTrack
import dev.olog.domain.entity.track.Album
import dev.olog.domain.entity.track.Artist
import dev.olog.domain.entity.track.GeneratedPlaylist
import dev.olog.domain.entity.track.Song
import dev.olog.domain.gateway.spotify.SpotifyGateway
import dev.olog.domain.gateway.track.AlbumGateway
import dev.olog.domain.gateway.track.ArtistGateway
import dev.olog.domain.gateway.track.TrackGateway
import dev.olog.lib.network.retrofit.IoResult
import dev.olog.lib.network.retrofit.fix
import dev.olog.lib.network.retrofit.flatMap
import dev.olog.lib.network.retrofit.map
import dev.olog.shared.coroutines.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import me.xdrop.fuzzywuzzy.FuzzySearch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class SpotifyRepository @Inject constructor(
    private val artistGateway: ArtistGateway,
    private val albumGateway: AlbumGateway,
    private val service: SpotifyService,
    private val imageDao: SpotifyImagesDao,
    private val workManager: WorkManager,
    private val generatedPlaylistsDao: GeneratedPlaylistsDao,
    private val trackGateway: TrackGateway
) : SpotifyGateway {

    override suspend fun getArtistAlbums(
        artistMediaId: MediaId.Category,
        type: SpotifyAlbumType
    ): List<SpotifyAlbum> {
        val artist = artistGateway.getByParam(artistMediaId.categoryId.toLong())!!

        return findSpotifyArtistBestMatch(artist)
            .flatMap { service.getArtistAlbums(it.id, type.value) }
            .map { artistAlbums ->
                artistAlbums.items.map { it.toDomain() }.distinctBy { it.title }
            }
            .fix(orDefault = emptyList())
            .also { albums ->
                imageDao.insertImages(albums.map { SpotifyImageEntity(it.uri, it.image) })
            }
    }

    override suspend fun getArtistTopTracks(artistMediaId: MediaId.Category): List<SpotifyTrack> {
        val artist = artistGateway.getByParam(artistMediaId.categoryId.toLong())!!

        return findSpotifyArtistBestMatch(artist)
            .flatMap { service.getArtistTopTracks(it.id) }
            .map { topTracks ->
                topTracks.tracks.map { it.toDomain() }
            }
            .fix(orDefault = emptyList())
            .also { tracks ->
                imageDao.insertImages(tracks.map { SpotifyImageEntity(it.uri, it.image) })
            }
    }

    override fun getImage(spotifyUri: String): String? {
        return imageDao.getImage(spotifyUri)
    }

    override suspend fun getAlbumTracks(albumMediaId: MediaId.Category): List<SpotifyTrack> {
        val album = albumGateway.getByParam(albumMediaId.categoryId.toLong())!!

        return findSpotifyAlbumBestMatch(album)
            .flatMap { service.getAlbumTracks(it.id) }
            .map { it.items }
            .map { tracks ->
                tracks.map { it.toDomain() }
            }.fix(orDefault = emptyList())
        // not inserting images because spotify api doesn't return an image here
    }

    override suspend fun getTrack(trackId: String): SpotifyTrack? {
        val result = service.getTrack(trackId)
        if (result is IoResult.Success) {
            return result.data.toDomain().also {
                imageDao.insertImages(listOf(SpotifyImageEntity(it.uri, it.image)))
            }
        }
        return null
    }

    private suspend fun findSpotifyArtistBestMatch(artist: Artist): IoResult<RemoteSpotifyArtist> {
        try {
            return service.searchArtist("artist:${artist.name}")
                .map { it.artists.items }
                .map { artists ->
                    val bestIndex =
                        FuzzySearch.extractOne(artist.name, artists.map { it.name }).index
                    artists[bestIndex]
                }
        } catch (ex: NoSuchElementException) {
            return IoResult.Error.Generic(ex)
        }
    }

    private suspend fun findSpotifyAlbumBestMatch(album: Album): IoResult<RemoteSpotifyAlbum> {
        try {
            return service.searchAlbum("album:${album.title} artist:${album.artist}")
                .map { it.albums.items }
                .map { albums ->
                    val bestIndex =
                        FuzzySearch.extractOne(album.title, albums.map { it.name }).index
                    albums[bestIndex]
                }
        } catch (ex: NoSuchElementException) {
            return IoResult.Error.Generic(ex)
        }
    }


    override fun fetchTracks() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val work = OneTimeWorkRequestBuilder<SpotifyTrackFetcherWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
            .addTag(SpotifyTrackFetcherWorker.TAG)
            .build()

        workManager.enqueueUniqueWork(
            SpotifyTrackFetcherWorker.TAG,
            ExistingWorkPolicy.KEEP,
            work
        )
    }

    override fun observeFetchStatus(): Flow<Int> {
        val fetchTrackFlow = workManager
            .getWorkInfosByTagLiveData(SpotifyTrackFetcherWorker.TAG)
            .asFlow()
            .map {
                if (it != null && it.isNotEmpty()) {
                    it[0].progress.getInt(SpotifyTrackFetcherWorker.PROGRESS, 0)
                } else {
                    -1
                }
            }

        val fetchTrackAudioFeatureFlow = workManager
            .getWorkInfosByTagLiveData(SpotifyTrackAudioFeatureFetcherWorker.TAG)
            .asFlow()
            .map {
                if (it != null && it.isNotEmpty()) {
                    it[0].progress.getInt(SpotifyTrackAudioFeatureFetcherWorker.PROGRESS, 0)
                } else {
                    -1
                }
            }

        return fetchTrackFlow.combine(fetchTrackAudioFeatureFlow) { tracks, audio ->
            println("tracks $tracks, audio $audio") // TODO remove print
            when {
                tracks == -1 && audio == -1 -> -1 // both finished
                tracks == -1 -> 50 + audio / 2
                audio == -1 -> tracks / 2
                else -> -1

    override fun observePlaylists(): Flow<List<GeneratedPlaylist>> {
        return generatedPlaylistsDao.observePlaylists()
            .mapListItem { it.toPlaylist() }
    }

    override fun observePlaylistByParam(id: Long): Flow<GeneratedPlaylist> {
        return generatedPlaylistsDao.observePlaylistById(id)
            .map { it.toPlaylist() }
    }

    override fun getPlaylistsTracks(id: Long): List<Song> {
        val tracks = trackGateway.getAllTracks()
        val playlist = generatedPlaylistsDao.getPlaylistById(id)
        return tracks.filter { it.id in playlist.tracks }

    }

    override fun observePlaylistsTracks(id: Long): Flow<List<Song>> {
        return generatedPlaylistsDao.observePlaylistById(id).combine(trackGateway.observeAllTracks()) {
                playlist, tracks -> tracks.filter { it.id in playlist.tracks }
        }
    }
}
