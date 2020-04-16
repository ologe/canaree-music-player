package dev.olog.data.repository.lastfm

import android.provider.MediaStore
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
import dev.olog.data.api.DeezerService
import dev.olog.data.api.LastFmService
import dev.olog.data.mapper.LastFmNulls
import dev.olog.data.mapper.toDomain
import dev.olog.data.model.deezer.DeezerArtistResponse
import dev.olog.lib.network.QueryNormalizer
import dev.olog.lib.network.networkCall
import dev.olog.lib.network.safeNetworkCall
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
    }

    // track
    override suspend fun mustFetchTrack(trackId: Long): Boolean {
        assertBackgroundThread()
        val mustFetch = localTrack.mustFetch(trackId)
        Timber.v("$TAG must fetch track id=$trackId -> $mustFetch")
        return mustFetch
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
            // removes content between parenthesis
            .replace("(\\(|\\[)[\\w\\s]+(\\)|\\])".toRegex(), "")
            .trim()

        val trackArtist = if (song.artist == MediaStore.UNKNOWN_STRING) "" else song.artist

        val calls = listOf(
            async { fetchTrackLastFm(song, trackTitle, trackArtist) },
            async { fetchTrackDeezer(trackTitle, trackArtist) }
        ).awaitAll()

        val result = makeTrack(calls[0] as LastFmTrack, calls[1] as String?)
        localTrack.cache(result)
        return@coroutineScope result
    }

    private fun makeTrack(lastFmTrack: LastFmTrack, image: String?): LastFmTrack {
        return LastFmTrack(
            lastFmTrack.id,
            lastFmTrack.title,
            lastFmTrack.artist,
            lastFmTrack.album,
            image ?: lastFmTrack.image,
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
        try {
            val trackId = song.id

            var result: LastFmTrack? = null
            if (song.artist != MediaStore.UNKNOWN_STRING) { // search only if has artist
                result = networkCall { lastFmService.getTrackInfoAsync(trackTitle, trackArtist) }
                    ?.toDomain(trackId)
            }
            if (result == null) {
                val searchTrack = networkCall { lastFmService.searchTrackAsync(trackTitle, trackArtist) }
                    ?.toDomain(trackId)

                if (searchTrack != null && searchTrack.title.isNotBlank() && searchTrack.artist.isNotBlank()) {
                    result = networkCall { lastFmService.getTrackInfoAsync(searchTrack.title, searchTrack.artist) }
                        ?.toDomain(trackId)
                }
                if (result == null) {
                    result = LastFmNulls.createNullTrack(trackId).toDomain()
                }
            }
            return result
        } catch (ex: Exception){
            // TODO investigate nullity
            Timber.w(ex)
            return LastFmNulls.createNullTrack(song.id).toDomain()
        }
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
        try {
            return safeNetworkCall { deezerService.getTrack(query) }?.data?.get(0)?.album
                ?.getBestImage() ?: ""
        } catch (ex: Exception){
            Timber.w(ex)
            return null
        }
    }

    override suspend fun deleteTrack(trackId: Long) {
        assertBackgroundThread()
        localTrack.delete(trackId)
    }

    // album
    override suspend fun mustFetchAlbum(albumId: Long): Boolean {
        assertBackgroundThread()
        val mustFetch = localAlbum.mustFetch(albumId)
        Timber.v("$TAG must fetch album id=$albumId -> $mustFetch")
        return mustFetch
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

        val calls = listOf(
            async { fetchAlbumLastFm(album) },
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

    private suspend fun fetchAlbumLastFm(album: Album) : LastFmAlbum {
        try {
            val albumId = album.id

            var result: LastFmAlbum? = null
            if (album.title != MediaStore.UNKNOWN_STRING) {
                result = networkCall { lastFmService.getAlbumInfoAsync(album.title, album.artist) }
                    ?.toDomain(albumId)
            }

            if (result == null) {
                val searchAlbum = networkCall { lastFmService.searchAlbumAsync(album.title) }
                    ?.toDomain(albumId, album.artist)

                if (searchAlbum != null && searchAlbum.title.isNotBlank() && searchAlbum.artist.isNotBlank()) {
                    result = networkCall { lastFmService.getAlbumInfoAsync(searchAlbum.title, searchAlbum.artist) }
                        ?.toDomain(albumId)
                }
                if (result == null) {
                    result = LastFmNulls.createNullAlbum(albumId).toDomain()
                }
            }
            return result
        } catch (ex: Exception){
            Timber.w(ex)
            return LastFmNulls.createNullAlbum(album.id).toDomain()
        }
    }

    private suspend fun fetchAlbumDeezer(album: Album): String? {
        val query = if (album.artist.isBlank()){
            album.title
        } else {
            "${album.artist} - ${album.title}"
        }
        try {
            return safeNetworkCall { deezerService.getAlbum(query) }?.data?.get(0)
                ?.getBestImage() ?: ""
        } catch (ex: Exception){
            Timber.w(ex)
            return null
        }
    }

    override suspend fun deleteAlbum(albumId: Long) {
        assertBackgroundThread()
        localAlbum.delete(albumId)
    }

    // artist
    override suspend fun mustFetchArtist(artistId: Long): Boolean {
        assertBackgroundThread()
        val mustFetch = localArtist.mustFetch(artistId)
        Timber.v("$TAG must fetch artist id=$artistId -> $mustFetch")
        return mustFetch
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

        val calls = listOf(
            async {
                safeNetworkCall { lastFmService.getArtistInfoAsync(artist.name) }?.toDomain(artistId)
            },
            async {
                safeNetworkCall { deezerService.getArtist(artist.name) }
            }
        ).awaitAll()
        var result = makeArtist(artist, calls[0] as LastFmArtist?, calls[1] as DeezerArtistResponse?)

        if (result == null) {
            result = LastFmNulls.createNullArtist(artistId).toDomain()
        }
        localArtist.cache(result)
        return@coroutineScope result
    }

    private fun makeArtist(
        artist: Artist,
        lastFmArtist: LastFmArtist?,
        deezerResponse: DeezerArtistResponse?): LastFmArtist? {
        if (lastFmArtist == null && deezerResponse == null){
            return null
        }

        val imageUrl = deezerResponse?.data?.get(0)?.getBestImage() ?: ""

        return LastFmArtist(
            artist.id,
            imageUrl,
            lastFmArtist?.mbid ?: "",
            lastFmArtist?.wiki ?: ""
        )
    }

    override suspend fun deleteArtist(artistId: Long) {
        assertBackgroundThread()
        localArtist.delete(artistId)
    }
}