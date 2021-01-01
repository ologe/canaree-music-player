package dev.olog.domain.interactor

import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.PlaylistGateway
import javax.inject.Inject

class InsertHistorySongUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastGateway: PodcastPlaylistGateway

) {

    suspend operator fun invoke(param: Input) {
        if (param.isPodcast) {
            podcastGateway.insertPodcastToHistory(param.id)
        } else {
            playlistGateway.insertSongToHistory(param.id)
        }
    }

    class Input(
        val id: Long,
        val isPodcast: Boolean
    )

}