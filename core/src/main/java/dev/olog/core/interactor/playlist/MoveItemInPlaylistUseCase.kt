package dev.olog.core.interactor.playlist

import dev.olog.core.entity.PlaylistType
import dev.olog.core.gateway.track.PlaylistGateway
import javax.inject.Inject

class MoveItemInPlaylistUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway
) {

    fun execute(input: Input): Boolean {
        if (input.type == PlaylistType.PODCAST) {
            return false
        }
        return playlistGateway.moveItem(input.playlistId, input.from, input.to)
    }

    class Input(
        @JvmField
        val playlistId: Long,
        @JvmField
        val from: Int,
        @JvmField
        val to: Int,
        @JvmField
        val type: PlaylistType
    )

}