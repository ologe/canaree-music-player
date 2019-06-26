package dev.olog.msc.domain.interactor.playing.queue

import dev.olog.core.gateway.PlaylistGateway2
import dev.olog.core.gateway.PodcastPlaylistGateway
import kotlinx.coroutines.rx2.await
import javax.inject.Inject

class InsertHistorySongUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway2,
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