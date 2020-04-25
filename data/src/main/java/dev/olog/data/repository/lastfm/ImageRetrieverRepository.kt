package dev.olog.data.repository.lastfm

import android.provider.MediaStore
import dev.olog.data.api.DeezerService
import dev.olog.data.api.LastFmService
import dev.olog.data.mapper.LastFmNulls
import dev.olog.data.mapper.toDomain
import dev.olog.domain.entity.LastFmAlbum
import dev.olog.domain.entity.LastFmArtist
import dev.olog.domain.entity.LastFmTrack
import dev.olog.domain.entity.track.Album
import dev.olog.domain.entity.track.Artist
import dev.olog.domain.entity.track.Song
import dev.olog.domain.gateway.ImageRetrieverGateway
import dev.olog.domain.gateway.track.AlbumGateway
import dev.olog.domain.gateway.track.ArtistGateway
import dev.olog.domain.gateway.track.TrackGateway
import dev.olog.lib.network.QueryNormalizer
import dev.olog.lib.network.retrofit.*
import dev.olog.shared.android.utils.assertBackgroundThread
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import timber.log.Timber
import javax.inject.Inject

// TODO refactor
internal class ImageRetrieverRepository @Inject constructor(
    private val lastFmService: LastFmService,
    private val deezerService: DeezerService,
    private val localTrack: ImageRetrieverLocalTrack,
    private val localArtist: ImageRetrieverLocalArtist,
    private val localAlbum: ImageRetrieverLocalAlbum,
    private val trackGateway: TrackGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway

) : ImageRetrieverGateway {

    companion object {
        @JvmStatic
        private val TAG = "D:${ImageRetrieverRepository::class.java.simpleName}"
        // TODO test this strings usage
        private const val UNKNOWN_STRING = MediaStore.UNKNOWN_STRING
        private const val UNKNOWN = "unknown"
    }

    // region track
    override suspend fun getCachedTrack(trackId: Long): LastFmTrack? {
        Timber.v("$TAG get cached track id=$trackId")
        assertBackgroundThread()
        return localTrack.getCached(trackId)
    }

    override suspend fun getTrack(trackId: Long): LastFmTrack? = coroutineScope {
        Timber.v("$TAG get track id=$trackId")
        assertBackgroundThread()
        val cached = localTrack.getCached(trackId)
        if (cached != null) {
            Timber.v("$TAG found in cache id=$trackId")
            return@coroutineScope cached
        }
        Timber.v("$TAG fetch id=$trackId")

        val song = trackGateway.getByParam(trackId) ?: return@coroutineScope null

        val trackTitle = QueryNormalizer.normalize(song.title)

        var trackArtist = if (song.artist == UNKNOWN_STRING) "" else song.artist
        trackArtist = QueryNormalizer.normalize(trackArtist)

        val calls = listOf(
            async { fetchTrackLastFm(song, trackTitle, trackArtist) },
            async { fetchTrackDeezer(trackTitle, trackArtist) }
        ).awaitAll()

        val result = makeTrack(calls[0] as LastFmTrack, calls[1] as String?)
        localTrack.cache(result)
        return@coroutineScope result
    }

    private fun makeTrack(lastFmTrack: LastFmTrack, deezerImage: String?): LastFmTrack {
        return LastFmTrack(
            lastFmTrack.id,
            lastFmTrack.title,
            lastFmTrack.artist,
            lastFmTrack.album,
            deezerImage ?: lastFmTrack.image,
            lastFmTrack.mbid,
            lastFmTrack.artistMbid,
            lastFmTrack.albumMbid
        )
    }

    private suspend fun fetchTrackLastFm(
        song: Song,
        trackTitle: String,
        trackArtist: String
    ): LastFmTrack {
        val trackId = song.id

        var result: IoResult<LastFmTrack>? = null
        if (song.artist != UNKNOWN) { // search only if has artist
            result = lastFmService.getTrackInfo(trackTitle, trackArtist)
                .map { it.toDomain(trackId) }
                .takeIf { it is IoResult.Success }
        }
        if (result == null) {
            result = lastFmService.searchTrack(trackTitle, trackArtist)
                .map { it.toDomain(trackId) }
                .filter { it.title.isNotBlank() && it.artist.isNotBlank() }
                ?.flatMap { lastFmService.getTrackInfo(it.title, it.artist) }
                ?.map { it.toDomain(trackId) }
        }
        return result.orDefault(LastFmNulls.createNullTrack(trackId).toDomain())
    }

    private suspend fun fetchTrackDeezer(
        trackTitle: String,
        trackArtist: String
    ): String? {
        val query = if (trackArtist.isBlank()){
            trackTitle
        } else {
            "$trackTitle - $trackArtist"
        }

        return deezerService.getTrack(query)
            .map { it.data?.firstOrNull()?.album?.getBestImage() ?: "" }
            .filter { it.isNotBlank() }
            .orDefault("")
            .takeIf { it.isNotBlank() }
    }

    override suspend fun deleteTrack(trackId: Long) {
        assertBackgroundThread()
        localTrack.delete(trackId)
    }

    // endregion

    // region album
    override suspend fun getCachedAlbum(albumId: Long): LastFmAlbum? {
        Timber.v("$TAG get cached album id=$albumId")
        assertBackgroundThread()
        val album = albumGateway.getByParam(albumId) ?: return null
        if (album.hasSameNameAsFolder) {
            Timber.v("$TAG id=$albumId has same name as folder, skip")
            return null
        }

        return localAlbum.getCached(albumId)
    }

    override suspend fun getAlbum(albumId: Long): LastFmAlbum? = coroutineScope {
        Timber.v("$TAG get album id=$albumId")
        assertBackgroundThread()
        val album = albumGateway.getByParam(albumId) ?: return@coroutineScope null
        if (album.hasSameNameAsFolder) {
            Timber.v("$TAG id=$albumId has same name as folder, skip")
            return@coroutineScope null
        }

        val cached = localAlbum.getCached(albumId)
        if (cached != null) {
            Timber.v("$TAG found in cache id=$album")
            return@coroutineScope cached
        }
        Timber.v("$TAG fetch id=$albumId")

        val title = QueryNormalizer.normalize(album.title)
        var artist = if (album.artist == MediaStore.UNKNOWN_STRING) "" else album.artist
        artist = QueryNormalizer.normalize(artist)

        val calls = listOf(
            async { fetchAlbumLastFm(album.id, title, artist) },
            async { fetchAlbumDeezer(album) }
        ).awaitAll()

        val result = makeAlbum(calls[0] as LastFmAlbum, calls[1] as String?)

        localAlbum.cache(result)
        return@coroutineScope result
    }

    private fun makeAlbum(lastFmTrack: LastFmAlbum, image: String?): LastFmAlbum {
        return LastFmAlbum(
            lastFmTrack.id,
            lastFmTrack.title,
            lastFmTrack.artist,
            image ?: lastFmTrack.image,
            lastFmTrack.mbid,
            lastFmTrack.wiki
        )
    }

    private suspend fun fetchAlbumLastFm(
        albumId: Long,
        title: String,
        artist: String
    ) : LastFmAlbum {

        var result: IoResult<LastFmAlbum>? = null
        if (title != UNKNOWN) {
            result = lastFmService.getAlbumInfo(title, artist)
                .map { it.toDomain(albumId) }
                .takeIf { it is IoResult.Success }
        }

        if (result == null) {
            result = lastFmService.searchAlbum(title)
                .map { it.toDomain(albumId, artist) }
                .filter { it.title.isNotBlank() && it.artist.isNotBlank() }
                ?.flatMap { lastFmService.getAlbumInfo(it.title, it.artist) }
                ?.map { it.toDomain(albumId) }
        }
        return result.orDefault(LastFmNulls.createNullAlbum(albumId).toDomain())
    }

    private suspend fun fetchAlbumDeezer(album: Album): String? {
        val query = if (album.artist.isBlank()){
            album.title
        } else {
            "${album.artist} - ${album.title}"
        }

        return deezerService.getAlbum(query)
            .map { it.data?.firstOrNull()?.getBestImage() ?: "" }
            .filter { it.isNotBlank() }
            .orDefault("")
            .takeIf { it.isNotBlank() }
    }

    override suspend fun deleteAlbum(albumId: Long) {
        assertBackgroundThread()
        localAlbum.delete(albumId)
    }

    // endregion

    // region artist
    override suspend fun getCachedArtist(artistId: Long): LastFmArtist? {
        Timber.v("$TAG get cached artist id=$artistId")
        assertBackgroundThread()
        return localArtist.getCached(artistId)
    }

    override suspend fun getArtist(artistId: Long): LastFmArtist? = coroutineScope {
        Timber.v("$TAG get artist id=$artistId")
        assertBackgroundThread()
        val cached = localArtist.getCached(artistId)
        if (cached != null) {
            Timber.v("$TAG found in cache id=$artistId")
            return@coroutineScope cached
        }
        Timber.v("$TAG fetch id=$artistId")

        val artist = artistGateway.getByParam(artistId) ?: return@coroutineScope null
        val name = QueryNormalizer.normalize(artist.name)

        val calls = listOf(
            async {
                lastFmService.getArtistInfo(name)
                    .map { it.toDomain(artistId) ?: LastFmNulls.createNullArtist(artistId).toDomain() }
                    .filterSuccess()
                    ?.data
            },
            async {
                deezerService.getArtist(name)
                    .map { it.data?.firstOrNull()?.getBestImage() ?: "" }
                    .fix("")
                    .takeIf { it.isNotBlank() }
            }
        ).awaitAll()
        val result = makeArtist(artist, calls[0] as LastFmArtist?, calls[1] as String)
        localArtist.cache(result)
        return@coroutineScope result
    }

    private fun makeArtist(
        artist: Artist,
        lastFmArtist: LastFmArtist?,
        deezerImage: String?
    ): LastFmArtist {
        if (lastFmArtist == null || deezerImage == null) {
            return LastFmNulls.createNullArtist(artist.id).toDomain()
        }

        return LastFmArtist(
            artist.id,
            deezerImage,
            lastFmArtist.mbid,
            lastFmArtist.wiki
        )
    }

    override suspend fun deleteArtist(artistId: Long) {
        assertBackgroundThread()
        localArtist.delete(artistId)
    }

    // endregion

}