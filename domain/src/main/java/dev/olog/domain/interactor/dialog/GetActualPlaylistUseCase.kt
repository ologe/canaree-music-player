package dev.olog.domain.interactor.dialog

import dev.olog.domain.entity.Playlist
import dev.olog.domain.gateway.PlaylistGateway
import javax.inject.Inject

class GetActualPlaylistUseCase @Inject internal constructor(
        private val gateway: PlaylistGateway

)  {

    fun execute(): List<Playlist>{
        return gateway.getActualPlaylistsBlocking()
    }
}
