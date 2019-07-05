package dev.olog.core.interactor

import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import kotlinx.coroutines.rx2.await
import javax.inject.Inject

class InsertHistorySongUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastGateway: PodcastPlaylistGateway

) {

    suspend operator fun invoke(param: Input) {
        if (param.isPodcast) {
            podcastGateway.insertPodcastToHistory(param.id).await()
        } else {
            playlistGateway.insertSongToHistory(param.id).await()
        }
    }

    class Input(
        val id: Long,
        val isPodcast: Boolean
    )

}