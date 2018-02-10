package dev.olog.msc.domain.interactor

import dev.olog.msc.domain.gateway.PlaylistGateway
import javax.inject.Inject

class MoveItemInPlaylistUseCase @Inject constructor(
        private val gateway: PlaylistGateway
) {

    fun execute(playlistId: Long, from: Int, to: Int): Boolean{
        return gateway.moveItem(playlistId, from, to)
    }

}