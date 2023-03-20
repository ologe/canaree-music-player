package dev.olog.data.repository.lastfm

import android.provider.MediaStore
import dev.olog.core.entity.LastFmAlbum
import dev.olog.core.entity.LastFmArtist
import dev.olog.core.entity.LastFmTrack
import dev.olog.core.gateway.ImageRetrieverGateway
import dev.olog.core.gateway.track.AlbumGateway
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.getOrNull
import dev.olog.data.api.deezer.DeezerService
import dev.olog.data.api.deezer.DeezerArtistResponse
import dev.olog.data.api.lastfm.LastFmService
import dev.olog.data.mapper.LastFmNulls
import dev.olog.data.mapper.toDomain
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

// TODO fix returning null on any error
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

    // track
    override suspend fun mustFetchTrack(trackId: Long): Boolean {
        return localTrack.mustFetch(trackId)
    }

    override suspend fun getTrack(trackId: Long): LastFmTrack? = coroutineScope {
        val cached = localTrack.getCached(trackId)
        if (cached != null) {
            return@coroutineScope cached
        }
        val song = songGateway.getById(trackId) ?: return@coroutineScope null

        val title = QuerySanitizer.sanitize(song.title)
        val artist = QuerySanitizer.sanitize(song.artist)

        val lastFmCall = async { fetchTrackLastFm(song.id, song.artist, title, artist) }
        val deezerCall = async { fetchTrackDeezer(title, artist) }

        val result = makeTrack(lastFmCall.await(), deezerCall.await())
        localTrack.cache(result)
        return@coroutineScope result
    }

    private fun makeTrack(lastFmTrack: LastFmTrack, image: String?): LastFmTrack {
        return LastFmTrack(
            id = lastFmTrack.id,
            title = lastFmTrack.title,
            artist = lastFmTrack.artist,
            album = lastFmTrack.album,
            image = image ?: lastFmTrack.image,
            mbid = lastFmTrack.mbid,
            artistMbid = lastFmTrack.artistMbid,
            albumMbid = lastFmTrack.albumMbid
        )
    }

    private suspend fun fetchTrackLastFm(
        id: Long,
        originalArtist: String,
        title: String,
        artist: String
    ): LastFmTrack {
        if (originalArtist != MediaStore.UNKNOWN_STRING) { // search only if has artist
            val result = lastFmService.getTrackInfo(title, artist).getOrNull()
                ?.toDomain(id)
            if (result != null) {
                return result
            }
        }
        val searchTrack = lastFmService.searchTrack(title, artist).getOrNull()
            ?.toDomain(id)

        if (searchTrack != null && searchTrack.title.isNotBlank() && searchTrack.artist.isNotBlank()) {
            val result = lastFmService.getTrackInfo(searchTrack.title, searchTrack.artist).getOrNull()
                ?.toDomain(id)
            if (result != null) {
                return result
            }
        }
        return LastFmNulls.createNullTrack(id).toDomain()
    }

    private suspend fun fetchTrackDeezer(
        title: String,
        artist: String
    ): String? {
        val query = if (artist.isBlank()) title else "$title - $artist"
        return deezerService.getTrack(query).getOrNull()?.data?.getOrNull(0)?.album?.let {
            when {
                it.coverXl?.isNotEmpty() == true -> it.coverXl
                it.coverBig?.isNotEmpty() == true -> it.coverBig
                it.coverMedium?.isNotEmpty() == true -> it.coverMedium
                it.coverSmall?.isNotEmpty() == true -> it.coverSmall
                it.cover?.isNotEmpty() == true -> it.cover
                else -> null
            }
        }
    }

    override suspend fun deleteTrack(trackId: Long) {
        localTrack.delete(trackId)
    }

    // album
    override suspend fun mustFetchAlbum(albumId: Long): Boolean {
        return localAlbum.mustFetch(albumId)
    }

    override suspend fun getAlbum(albumId: Long): LastFmAlbum? = coroutineScope {
        val album = albumGateway.getByParam(albumId) ?: return@coroutineScope null
        if (album.hasSameNameAsFolder) {
            return@coroutineScope null
        }

        val cached = localAlbum.getCached(albumId)
        if (cached != null) {
            return@coroutineScope cached
        }

        val title = QuerySanitizer.sanitize(album.title)
        val artist = QuerySanitizer.sanitize(album.artist)

        val lastFmCall = async { fetchAlbumLastFm(album.id, album.title, title, artist) }
        val deezerCall = async { fetchAlbumDeezer(title, artist) }

        val result = makeAlbum(lastFmCall.await(), deezerCall.await())
        localAlbum.cache(result)
        return@coroutineScope result
    }

    private fun makeAlbum(lastFmTrack: LastFmAlbum, image: String?): LastFmAlbum {
        return LastFmAlbum(
            id = lastFmTrack.id,
            title = lastFmTrack.title,
            artist = lastFmTrack.artist,
            image = image ?: lastFmTrack.image,
            mbid = lastFmTrack.mbid,
            wiki = lastFmTrack.wiki
        )
    }

    private suspend fun fetchAlbumLastFm(
        id: Long,
        originalTitle: String,
        title: String,
        artist: String,
    ) : LastFmAlbum {

        if (originalTitle != MediaStore.UNKNOWN_STRING) {
            val result = lastFmService.getAlbumInfo(title, artist).getOrNull()
                ?.toDomain(id)
            if (result != null) {
                return result
            }
        }

        val searchAlbum = lastFmService.searchAlbum(title).getOrNull()
            ?.toDomain(id, artist)

        if (searchAlbum != null && searchAlbum.title.isNotBlank() && searchAlbum.artist.isNotBlank()) {
            val result = lastFmService.getAlbumInfo(searchAlbum.title, searchAlbum.artist).getOrNull()
                ?.toDomain(id)
            if (result != null) {
                return result
            }
        }
        return LastFmNulls.createNullAlbum(id).toDomain()
    }

    private suspend fun fetchAlbumDeezer(
        title: String,
        artist: String
    ): String? {
        val query = if (artist.isBlank()) title else "$artist - $title"
        return deezerService.getAlbum(query).getOrNull()?.data?.getOrNull(0)?.let {
            when {
                it.coverXl?.isNotEmpty() == true -> it.coverXl
                it.coverBig?.isNotEmpty() == true -> it.coverBig
                it.coverMedium?.isNotEmpty() == true -> it.coverMedium
                it.coverSmall?.isNotEmpty() == true -> it.coverSmall
                it.cover?.isNotEmpty() == true -> it.cover
                else -> null
            }
        }
    }

    override suspend fun deleteAlbum(albumId: Long) {
        localAlbum.delete(albumId)
    }

    // artist
    override suspend fun mustFetchArtist(artistId: Long): Boolean {
        return localArtist.mustFetch(artistId)
    }

    override suspend fun getArtist(artistId: Long): LastFmArtist? = coroutineScope {
        val cached = localArtist.getCached(artistId)
        if (cached != null) {
            return@coroutineScope cached
        }

        val artist = artistGateway.getByParam(artistId) ?: return@coroutineScope null
        val artistName = QuerySanitizer.sanitize(artist.name)

        val lastFmCall = async { lastFmService.getArtistInfo(artistName).getOrNull()?.toDomain(artistId) }
        val deezerCall = async { deezerService.getArtist(artistName).getOrNull() }

        var result = makeArtist(artist.id, lastFmCall.await(), deezerCall.await())

        if (result == null) {
            result = LastFmNulls.createNullArtist(artistId).toDomain()
        }
        localArtist.cache(result)
        return@coroutineScope result
    }

    private fun makeArtist(
        id: Long,
        lastFmArtist: LastFmArtist?,
        deezerResponse: DeezerArtistResponse?
    ): LastFmArtist? {
        if (lastFmArtist == null && deezerResponse == null){
            return null
        }

        val imageUrl = deezerResponse?.data?.getOrNull(0)?.let {
            when {
                it.pictureXl?.isNotEmpty() == true -> it.pictureXl
                it.pictureBig?.isNotEmpty() == true -> it.pictureBig
                it.pictureMedium?.isNotEmpty() == true -> it.pictureMedium
                it.pictureSmall?.isNotEmpty() == true -> it.pictureSmall
                it.picture?.isNotEmpty() == true -> it.picture
                else -> null
            }
        }

        return LastFmArtist(
            id = id,
            image = imageUrl.orEmpty(),
            mbid = lastFmArtist?.mbid ?: "",
            wiki = lastFmArtist?.wiki ?: ""
        )
    }

    override suspend fun deleteArtist(artistId: Long) {
        localArtist.delete(artistId)
    }
}