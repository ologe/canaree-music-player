package dev.olog.core.interactor

import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import javax.inject.Inject

class InsertHistorySongUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastGateway: PodcastPlaylistGateway

) {

    suspend operator fun invoke(param: Input) {
        if (param.isPodcast) {
            podcastGateway.insertSongToHistory(param.id)
        } else {
            playlistGateway.insertSongToHistory(param.id)
        }
    }

    class Input(
        val id: Long,
        val isPodcast: Boolean
    )

}