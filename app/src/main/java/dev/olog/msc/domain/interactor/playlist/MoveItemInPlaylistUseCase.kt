package dev.olog.msc.domain.interactor.playlist

import dev.olog.msc.domain.entity.PlaylistType
import dev.olog.msc.domain.gateway.PlaylistGateway
import dev.olog.msc.domain.gateway.PodcastPlaylistGateway
import javax.inject.Inject

class MoveItemInPlaylistUseCase @Inject constructor(
        private val playlistGateway: PlaylistGateway,
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