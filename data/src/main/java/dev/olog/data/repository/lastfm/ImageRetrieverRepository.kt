package dev.olog.data.repository.lastfm

import android.provider.MediaStore
import android.util.Log
import dev.olog.core.entity.LastFmAlbum
import dev.olog.core.entity.LastFmArtist
import dev.olog.core.entity.LastFmTrack
import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.ImageRetrieverGateway
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.track.AlbumGateway
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.data.api.deezer.DeezerService
import dev.olog.data.api.deezer.DeezerArtistResponse
import dev.olog.data.api.lastfm.LastFmService
import dev.olog.data.mapper.LastFmNulls
import dev.olog.data.mapper.toDomain
import dev.olog.shared.TextUtils
import dev.olog.data.utils.assertBackgroundThread
import dev.olog.data.utils.networkCall
import dev.olog.data.utils.safeNetworkCall
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

// TODO refactor
internal class ImageRetrieverRepository @Inject constructor(
    private val lastFmService: LastFmService,
    private val deezerService: DeezerService,
    private val localTrack: ImageRetrieverLocalTrack,
    private val localArtist: ImageRetrieverLocalArtist,
    private val localAlbum: ImageRetrieverLocalAlbum,
    private val songGateway: SongGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway

) : ImageRetrieverGateway {

    companion object {
        private val TAG = "D:${ImageRetrieverRepository::class.java.simpleName}"
    }

    // track
    override suspend fun mustFetchTrack(trackId: Id): Boolean {
        assertBackgroundThread()
        val mustFetch = localTrack.mustFetch(trackId)
        Log.v(TAG, "must fetch track id=$trackId -> $mustFetch")
        return mustFetch
    }

    override suspend fun getTrack(trackId: Id): LastFmTrack? = coroutineScope {
        Log.v(TAG, "get track id=$trackId")
        assertBackgroundThread()
        val cached = localTrack.getCached(trackId)
        if (cached != null) {
            Log.v(TAG, "found in cache id=$trackId")
            return@coroutineScope cached
        }
        Log.v(TAG, "fetch id=$trackId")

        val song = songGateway.getByParam(trackId) ?: return@coroutineScope null

        val trackTitle = TextUtils.addSpacesToDash(song.title)
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
        } catch (ex: Throwable){
            ex.printStackTrace()
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
            return safeNetworkCall { deezerService.getTrack(query) }?.data?.get(0)?.album?.let {
                when {
                    it.coverXl.isNotEmpty() -> it.coverXl
                    it.coverBig.isNotEmpty() -> it.coverBig
                    it.coverMedium.isNotEmpty() -> it.coverMedium
                    it.coverSmall.isNotEmpty() -> it.coverSmall
                    it.cover.isNotEmpty() -> it.cover
                    else -> ""
                }
            } ?: ""
        } catch (ex: Throwable){
            ex.printStackTrace()
            return null
        }
    }

    override suspend fun deleteTrack(trackId: Id) {
        assertBackgroundThread()
        localTrack.delete(trackId)
    }

    // album
    override suspend fun mustFetchAlbum(albumId: Id): Boolean {
        assertBackgroundThread()
        val mustFetch = localAlbum.mustFetch(albumId)
        Log.v(TAG, "must fetch album id=$albumId -> $mustFetch")
        return mustFetch
    }

    override suspend fun getAlbum(albumId: Id): LastFmAlbum? = coroutineScope {
        Log.v(TAG, "get album id=$albumId")
        assertBackgroundThread()
        val album = albumGateway.getByParam(albumId) ?: return@coroutineScope null
        if (album.hasSameNameAsFolder) {
            Log.v(TAG, "id=$albumId has same name as folder, skip")
            return@coroutineScope null
        }

        val cached = localAlbum.getCached(albumId)
        if (cached != null) {
            Log.v(TAG, "found in cache id=$album")
            return@coroutineScope cached
        }
        Log.v(TAG, "fetch id=$albumId")

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
        } catch (ex: Throwable){
            ex.printStackTrace()
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
            return safeNetworkCall { deezerService.getAlbum(query) }?.data?.get(0)?.let {
                when {
                    it.coverXl.isNotEmpty() -> it.coverXl
                    it.coverBig.isNotEmpty() -> it.coverBig
                    it.coverMedium.isNotEmpty() -> it.coverMedium
                    it.coverSmall.isNotEmpty() -> it.coverSmall
                    it.cover.isNotEmpty() -> it.cover
                    else -> ""
                }
            } ?: ""
        } catch (ex: Throwable){
            ex.printStackTrace()
            return null
        }
    }

    override suspend fun deleteAlbum(albumId: Id) {
        assertBackgroundThread()
        localAlbum.delete(albumId)
    }

    // artist
    override suspend fun mustFetchArtist(artistId: Id): Boolean {
        assertBackgroundThread()
        val mustFetch = localArtist.mustFetch(artistId)
        Log.v(TAG, "must fetch artist id=$artistId -> $mustFetch")
        return mustFetch
    }

    override suspend fun getArtist(artistId: Id): LastFmArtist? = coroutineScope {
        Log.v(TAG, "get artist id=$artistId")
        assertBackgroundThread()
        val cached = localArtist.getCached(artistId)
        if (cached != null) {
            Log.v(TAG, "found in cache id=$artistId")
            return@coroutineScope cached
        }
        Log.v(TAG, "fetch id=$artistId")

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

        val imageUrl = deezerResponse?.data?.get(0)?.let {
            when {
                it.pictureXl.isNotEmpty() -> it.pictureXl
                it.pictureBig.isNotEmpty() -> it.pictureBig
                it.pictureMedium.isNotEmpty() -> it.pictureMedium
                it.pictureSmall.isNotEmpty() -> it.pictureSmall
                it.picture.isNotEmpty() -> it.picture
                else -> ""
            }
        } ?: ""

        return LastFmArtist(
            artist.id,
            imageUrl,
            lastFmArtist?.mbid ?: "",
            lastFmArtist?.wiki ?: ""
        )
    }

    override suspend fun deleteArtist(artistId: Id) {
        assertBackgroundThread()
        localArtist.delete(artistId)
    }
}