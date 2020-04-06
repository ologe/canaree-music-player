package dev.olog.domain.interactor.playlist

import dev.olog.domain.entity.PlaylistType
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.PlaylistGateway
import javax.inject.Inject

class MoveItemInPlaylistUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway
) {

    suspend operator fun invoke(input: Input) {
        return when (input.type){
            PlaylistType.PODCAST -> podcastPlaylistGateway.moveItem(input.playlistId, input.moveList)
            PlaylistType.TRACK -> playlistGateway.moveItem(input.playlistId, input.moveList)
        }
    }

    data class Input(
        val playlistId: Long,
        val moveList: List<Pair<Int, Int>>,
        val type: PlaylistType
    )

}