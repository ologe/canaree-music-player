package dev.olog.data.repository.lastfm.remote

import androidx.annotation.VisibleForTesting
import dev.olog.core.entity.LastFmAlbum
import dev.olog.core.entity.track.Album
import dev.olog.data.api.deezer.DeezerService
import dev.olog.data.api.lastfm.LastFmService
import dev.olog.data.mapper.LastFmNulls
import dev.olog.lib.network.model.getOrNull
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

internal class ImageRetrieverRemoteAlbum @Inject constructor(
    private val lastFmService: LastFmService,
    private val deezerService: DeezerService,
) {

    suspend fun fetch(
        album: Album
    ): LastFmAlbum = coroutineScope {
        val calls = listOf(
            async { fetchLastFmAlbumImage(album) },
            async { fetchDeezerAlbumImage(album) }
        ).awaitAll()

        val track = calls[0] as LastFmAlbum
        val deezerImage = calls[1] as String?
        track.copy(
            image = deezerImage ?: track.image
        )
    }

    @VisibleForTesting
    internal suspend fun fetchLastFmAlbumImage(album: Album): LastFmAlbum {
        val albumId = album.id

        if (album.hasUnknownTitle) {
            return LastFmNulls.createNullAlbum(albumId)
        }

        var result: LastFmAlbum? = null
        if (!album.hasUnknownArtist) {
            result = lastFmService.getAlbumInfo(album)
        }

        return result
            ?: lastFmService.searchAlbum(album)
            ?: LastFmNulls.createNullAlbum(albumId)
    }

    @VisibleForTesting
    internal suspend fun fetchDeezerAlbumImage(album: Album): String? {
        val query = if (album.artist.isBlank()) {
            album.title
        } else {
            "${album.artist} - ${album.title}"
        }
        return deezerService.getAlbum(query).getOrNull()?.bestCover
    }

}