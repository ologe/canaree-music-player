package dev.olog.data.remote

import androidx.annotation.VisibleForTesting
import dev.olog.core.entity.LastFmTrack
import dev.olog.core.entity.track.Track
import dev.olog.data.remote.deezer.DeezerService
import dev.olog.data.remote.lastfm.LastFmService
import dev.olog.lib.network.QueryNormalizer
import dev.olog.lib.network.model.getOrNull
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

interface ImageRetrieverRemoteTrack {

    suspend fun fetch(track: Track): LastFmTrack

}

internal class ImageRetrieverRemoteTrackImpl @Inject constructor(
    private val lastFmService: LastFmService,
    private val deezerService: DeezerService,
) : ImageRetrieverRemoteTrack {

    override suspend fun fetch(track: Track): LastFmTrack = coroutineScope {

        val trackTitle = QueryNormalizer.normalize(track.title)

        val trackArtist = QueryNormalizer.normalize(
            original = if (track.hasUnknownArtist) "" else track.artist
        )

        val calls = listOf(
            async { fetchLastFmTrack(track, trackTitle, trackArtist) },
            async { fetchDeezerTrackImage(trackTitle, trackArtist) }
        ).awaitAll()

        val lastFmTrack = calls[0] as LastFmTrack
        val deezerImage = calls[1] as String?

        lastFmTrack.copy(
            image = deezerImage ?: lastFmTrack.image
        )
    }

    @VisibleForTesting
    internal suspend fun fetchLastFmTrack(
        track: Track,
        trackTitle: String,
        trackArtist: String
    ): LastFmTrack {
        val trackId = track.id

        var result: LastFmTrack? = null
        if (!track.hasUnknownArtist) {
            // search only if has valid artist
            result = lastFmService.getTrackInfo(trackId, trackTitle, trackArtist)
        }
        return result
            ?: lastFmService.searchTrack(trackId, trackTitle, trackArtist)
            ?: LastFmNulls.createNullTrack(trackId)
    }

    @VisibleForTesting
    internal suspend fun fetchDeezerTrackImage(
        trackTitle: String,
        trackArtist: String
    ): String? {
        val query = if (trackArtist.isBlank()) {
            trackTitle
        } else {
            "$trackTitle - $trackArtist"
        }

        return deezerService.getTrack(query).getOrNull()?.bestCover
    }

}