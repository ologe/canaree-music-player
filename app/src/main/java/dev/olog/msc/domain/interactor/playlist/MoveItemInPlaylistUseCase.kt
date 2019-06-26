package dev.olog.msc.domain.interactor.playlist

import dev.olog.core.gateway.PlaylistGateway
import dev.olog.presentation.model.PlaylistType
import javax.inject.Inject

class MoveItemInPlaylistUseCase @Inject constructor(
        private val playlistGateway: PlaylistGateway
) {

    fun execute(input: Input): Boolean {
        val (playlistId, from, to, type) = input
        if (type == PlaylistType.PODCAST) {
            return false
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