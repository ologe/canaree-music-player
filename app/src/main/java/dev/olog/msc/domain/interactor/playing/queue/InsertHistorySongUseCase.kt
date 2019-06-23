package dev.olog.msc.domain.interactor.playing.queue

import dev.olog.msc.domain.gateway.PlaylistGateway
import dev.olog.msc.domain.gateway.PodcastPlaylistGateway
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