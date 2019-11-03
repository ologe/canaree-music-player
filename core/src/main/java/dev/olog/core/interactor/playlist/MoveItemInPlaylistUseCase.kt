package dev.olog.core.interactor.playlist

import dev.olog.core.entity.PlaylistType
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import javax.inject.Inject

class MoveItemInPlaylistUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway
) {

    suspend fun execute(input: Input) {
        if (input.type == PlaylistType.PODCAST) {
            podcastPlaylistGateway.moveItem(input.playlistId, input.moveList)
        } else {
            playlistGateway.moveItem(input.playlistId, input.moveList)
        }
    }

    data class Input(
        val playlistId: Long,
        val moveList: List<Pair<Int, Int>>,
        val type: PlaylistType
    )

}