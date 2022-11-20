package dev.olog.data.repository.image

import dev.olog.core.*
import dev.olog.core.entity.*
import dev.olog.core.gateway.ImageRetrieverGateway
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.track.AlbumGateway
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.data.api.deezer.DeezerService
import dev.olog.data.api.lastfm.LastFmService
import dev.olog.data.mapper.toDomain
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

internal class ImageRetrieverRepository @Inject constructor(
    private val lastFmService: LastFmService,
    private val deezerService: DeezerService,
    private val songGateway: SongGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
) : ImageRetrieverGateway {

    companion object {
        private const val DEEZER_PLACEHOLDER = "/images/artist//"
    }

    override suspend fun getSong(trackId: Id): IoResult<LastFmTrack?> {
        val song = songGateway.getByParam(trackId)
            ?: return IoResult.Failure.Unknown(IllegalArgumentException("track not found for id $trackId"))

        val title = QuerySanitizer.sanitize(song.title)
        val artist = QuerySanitizer.sanitize(song.artist)

        return fetchLastFmSong(trackId, title, artist)
            .map {
                val lastFm = it ?: return@map null
                val deezerImage = fetchDeezerSong(title, artist).getOrNull()
                LastFmTrack(
                    id = trackId,
                    title = lastFm.title,
                    artist = lastFm.artist,
                    album = lastFm.album,
                    // prefer deezer image, has better quality
                    image = deezerImage ?: lastFm.image,
                    mbid = lastFm.mbid,
                    artistMbid = lastFm.artistMbid,
                    albumMbid = lastFm.albumMbid,
                )
            }
    }

    override suspend fun fetchSongImage(
        trackId: Id
    ): ImageRetrieverResult<String> = coroutineScope {
        val song = songGateway.getByParam(trackId) ?: return@coroutineScope ImageRetrieverResult.NotFound

        val title = QuerySanitizer.sanitize(song.title)
        val artist = QuerySanitizer.sanitize(song.artist)

        val lastFmCall = async {
            fetchLastFmSong(trackId, title, artist).map { it?.image }.toFetchResult()
        }
        val deezerCall = async {
            fetchDeezerSong(title, artist).toFetchResult()
        }

        deezerCall.merge(lastFmCall)
    }

    private suspend fun fetchLastFmSong(
        id: Long,
        title: String,
        artist: String
    ): IoResult<LastFmTrack?> {
        return if (artist.isNotBlank()) {
            lastFmService.getTrackInfo(title, artist).map { it.toDomain(id) }
        } else {
            lastFmService.searchTrack(title, artist)
                .flatMap {
                    val searchTitle = it.title ?: return@flatMap IoResult.Success(null)
                    val searchArtist = it.artist ?: return@flatMap IoResult.Success(null)
                    lastFmService.getTrackInfo(searchTitle, searchArtist)
                }.map { it?.toDomain(id) }
        }
    }

    private suspend fun fetchDeezerSong(
        title: String,
        artist: String
    ): IoResult<String?> {
        val query = if (artist.isBlank()) title else "$title - $artist"
        return deezerService.getTrack(query)
            .map { it.data.firstOrNull()?.album }
            .map { album ->
                when {
                    album?.coverXl?.isNotEmpty() == true -> album.coverXl
                    album?.coverBig?.isNotEmpty() == true -> album.coverBig
                    album?.coverMedium?.isNotEmpty() == true -> album.coverMedium
                    album?.coverSmall?.isNotEmpty() == true -> album.coverSmall
                    else -> null
                }
            }
    }

    override suspend fun getAlbum(albumId: Id): IoResult<LastFmAlbum?> {
        val album = albumGateway.getByParam(albumId)
            .takeIf { it?.hasSameNameAsFolder == false }
            ?: return IoResult.Failure.Unknown(IllegalArgumentException("album not found for id $albumId"))

        val title = QuerySanitizer.sanitize(album.title)
        val artist = QuerySanitizer.sanitize(album.artist)

        return fetchLastFmAlbum(albumId, title, artist)
            .map {
                val lastFm = it ?: return@map null
                val deezerImage = fetchDeezerAlbum(title, artist).getOrNull()
                LastFmAlbum(
                    id = albumId,
                    title = lastFm.title,
                    artist = lastFm.artist,
                    // prefer deezer image, has better quality
                    image = deezerImage ?: lastFm.image,
                    mbid = lastFm.mbid,
                    wiki = lastFm.wiki,
                )
            }
    }

    override suspend fun fetchAlbumImage(albumId: Id): ImageRetrieverResult<String> = coroutineScope {
        val album = albumGateway.getByParam(albumId)
            .takeIf { it?.hasSameNameAsFolder == false }
            ?: return@coroutineScope ImageRetrieverResult.NotFound

        val title = QuerySanitizer.sanitize(album.title)
        val artist = QuerySanitizer.sanitize(album.artist)

        val lastFmCall = async {
            fetchLastFmAlbum(albumId, title, artist).map { it?.image }.toFetchResult()
        }
        val deezerCall = async {
            fetchDeezerAlbum(title, artist).toFetchResult()
        }

        deezerCall.merge(lastFmCall)
    }

    private suspend fun fetchLastFmAlbum(
        id: Long,
        title: String,
        artist: String
    ) : IoResult<LastFmAlbum?> {
        return if (title.isNotBlank()) {
            lastFmService.getAlbumInfo(title, artist).map { it.toDomain(id) }
        } else {
            lastFmService.searchAlbum(title)
                .flatMap {
                    val bestAlbum = it.findBestAlbum(artist) ?: return@flatMap IoResult.Success(null)
                    val searchTitle = bestAlbum.name ?: return@flatMap IoResult.Success(null)
                    val searchArtist = bestAlbum.artist ?: return@flatMap IoResult.Success(null)
                    lastFmService.getAlbumInfo(searchTitle, searchArtist)
                }
                .map { it?.toDomain(id) }
        }
    }

    private suspend fun fetchDeezerAlbum(
        title: String,
        artist: String,
    ): IoResult<String?> {
        val query = if (artist.isBlank()) title else "$artist - $title"
        return deezerService.getAlbum(query)
            .map { it.data.firstOrNull() }
            .map { data ->
                when {
                    data?.coverXl?.isNotEmpty() == true -> data.coverXl
                    data?.coverBig?.isNotEmpty() == true -> data.coverBig
                    data?.coverMedium?.isNotEmpty() == true -> data.coverMedium
                    data?.coverSmall?.isNotEmpty() == true -> data.coverSmall
                    else -> null
                }
            }
    }

    override suspend fun getArtist(artistId: Id): IoResult<LastFmArtist?> {
        val artist = artistGateway.getByParam(artistId)
            ?: return IoResult.Failure.Unknown(IllegalArgumentException("artist not found for id $artistId"))

        val name = QuerySanitizer.sanitize(artist.name)

        return fetchLastFmArtist(artistId, name)
            .map {
                val lastFm = it ?: return@map null
                val deezerImage = fetchDeezerArtist(name).getOrNull()
                LastFmArtist(
                    id = artistId,
                    image = deezerImage,
                    mbid = lastFm.mbid,
                    wiki = lastFm.wiki
                )
            }
    }

    override suspend fun fetchArtistImage(
        artistId: Id
    ): ImageRetrieverResult<String> {
        val artist = artistGateway.getByParam(artistId)
            ?: return ImageRetrieverResult.NotFound

        val name = QuerySanitizer.sanitize(artist.name)
        return fetchDeezerArtist(name)
            .map { it.takeIf { it?.contains(DEEZER_PLACEHOLDER) == false } }
            .toFetchResult()
    }

    private suspend fun fetchLastFmArtist(
        id: Long,
        name: String
    ) : IoResult<LastFmArtist?> {
        return lastFmService.getArtistInfo(name)
            .map { it.toDomain(id) }
    }

    private suspend fun fetchDeezerArtist(name: String): IoResult<String?> {
        return deezerService.getArtist(name)
            .map { it.data.firstOrNull() }
            .map { album ->
                when {
                    album?.pictureXl?.isNotEmpty() == true -> album.pictureXl
                    album?.pictureBig?.isNotEmpty() == true -> album.pictureBig
                    album?.pictureMedium?.isNotEmpty() == true -> album.pictureMedium
                    album?.pictureSmall?.isNotEmpty() == true -> album.pictureSmall
                    else -> null
                }
            }
    }

    private fun IoResult<String?>.toFetchResult(): ImageRetrieverResult<String> {
        return when (this) {
            is IoResult.Success -> {
                val v = this.value
                if (v != null) {
                    ImageRetrieverResult.Success(v)
                } else {
                    ImageRetrieverResult.NotFound
                }
            }
            is IoResult.Failure.Http -> {
                if (isServerError) {
                    ImageRetrieverResult.Error(exception)
                } else {
                    // rethrow non server errors
                    throw exception
                }
            } // propagate exception to see it
            is IoResult.Failure.Network -> ImageRetrieverResult.Error(exception)
            is IoResult.Failure.Unknown -> throw exception // rethrow unknown exception
        }
    }

}