package dev.olog.msc.domain.interactor.playlist

import dev.olog.core.gateway.PlaylistGateway2
import dev.olog.msc.domain.gateway.PodcastPlaylistGateway
import dev.olog.presentation.model.PlaylistType
import javax.inject.Inject

class MoveItemInPlaylistUseCase @Inject constructor(
        private val playlistGateway: PlaylistGateway2,
        private val podcastGateway: PodcastPlaylistGateway
) {

    fun execute(input: Input): Boolean{
        val (playlistId, from, to, type) = input
        if (type == PlaylistType.PODCAST){
            return podcastGateway.moveItem(playlistId, from, to)
        }
        return playlistGateway.moveItem(playlistId, from, to)
    }

    data class Input(
            val playlistId: Long,
            val from: Int,
            val to: Int,
            val type: PlaylistType
    )

}