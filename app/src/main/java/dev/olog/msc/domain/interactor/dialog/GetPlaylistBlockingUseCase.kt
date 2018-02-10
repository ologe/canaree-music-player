package dev.olog.msc.domain.interactor.dialog

import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.domain.gateway.PlaylistGateway
import javax.inject.Inject

class GetPlaylistBlockingUseCase @Inject internal constructor(
        private val gateway: PlaylistGateway

)  {

    fun execute(): List<Playlist>{
        return gateway.getPlaylistsBlocking()
    }
}
