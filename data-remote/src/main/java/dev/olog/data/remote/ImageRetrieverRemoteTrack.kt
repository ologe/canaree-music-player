package dev.olog.data.remote

import androidx.annotation.VisibleForTesting
import dev.olog.core.entity.LastFmTrack
import dev.olog.core.entity.track.Song
import dev.olog.data.remote.deezer.DeezerService
import dev.olog.data.remote.lastfm.LastFmService
import dev.olog.lib.network.QueryNormalizer
import dev.olog.lib.network.model.getOrNull
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

interface ImageRetrieverRemoteTrack {

    suspend fun fetch(song: Song): LastFmTrack

}

internal class ImageRetrieverRemoteTrackImpl @Inject constructor(
    private val lastFmService: LastFmService,
    private val deezerService: DeezerService,
) : ImageRetrieverRemoteTrack {

    override suspend fun fetch(song: Song): LastFmTrack = coroutineScope {

        val trackTitle = QueryNormalizer.normalize(song.title)

        val trackArtist = QueryNormalizer.normalize(
            original = if (song.hasUnknownArtist) "" else song.artist
        )

        val calls = listOf(
            async { fetchLastFmTrack(song, trackTitle, trackArtist) },
            async { fetchDeezerTrackImage(trackTitle, trackArtist) }
        ).awaitAll()

        val track = calls[0] as LastFmTrack
        val deezerImage = calls[1] as String?

        track.copy(
            image = deezerImage ?: track.image
        )
    }

    @VisibleForTesting
    internal suspend fun fetchLastFmTrack(
        song: Song,
        trackTitle: String,
        trackArtist: String
    ): LastFmTrack {
        val trackId = song.id

        var result: LastFmTrack? = null
        if (!song.hasUnknownArtist) {
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