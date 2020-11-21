package dev.olog.data.remote

import androidx.annotation.VisibleForTesting
import dev.olog.core.entity.LastFmArtist
import dev.olog.core.entity.track.Artist
import dev.olog.data.remote.deezer.DeezerService
import dev.olog.data.remote.lastfm.LastFmService
import dev.olog.data.remote.lastfm.dto.toDomain
import dev.olog.lib.network.model.getOrNull
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

interface ImageRetrieverRemoteArtist {

    suspend fun fetch(artist: Artist): LastFmArtist

}

internal class ImageRetrieverRemoteArtistImpl @Inject constructor(
    private val lastFmService: LastFmService,
    private val deezerService: DeezerService,
) : ImageRetrieverRemoteArtist {

    override suspend fun fetch(artist: Artist): LastFmArtist = coroutineScope {
        val calls = listOf(
            async { fetchLastFm(artist.id, artist.name) },
            async { fetchDeezerImage(artist.name) }
        ).awaitAll()

        val (lastFmArtist, deezerPicture) = calls

        makeArtist(
            artist = artist,
            lastFmArtist = lastFmArtist as LastFmArtist?,
            deezerPicture = deezerPicture as String?
        )
    }

    @VisibleForTesting
    internal suspend fun fetchLastFm(
        artistId: Long,
        artistName: String
    ): LastFmArtist? {
        return lastFmService.getArtistInfo(artistName).getOrNull()?.toDomain(artistId)
    }

    @VisibleForTesting
    internal suspend fun fetchDeezerImage(
        artistName: String
    ): String? {
        return deezerService.getArtist(artistName).getOrNull()?.bestPicture
    }

    private fun makeArtist(
        artist: Artist,
        lastFmArtist: LastFmArtist?,
        deezerPicture: String?
    ): LastFmArtist {
        if (lastFmArtist == null || deezerPicture == null) {
            return LastFmNulls.createNullArtist(artist.id)
        }

        return LastFmArtist(
            artist.id,
            deezerPicture,
            lastFmArtist.mbid,
            lastFmArtist.wiki
        )
    }

}