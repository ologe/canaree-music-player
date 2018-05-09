package dev.olog.msc.domain.interactor.all

import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.domain.gateway.PlaylistGateway
import javax.inject.Inject

class GetPlaylistsBlockingUseCase @Inject internal constructor(
        private val gateway: PlaylistGateway

)  {

    fun execute(): List<Playlist>{
        return gateway.getPlaylistsBlocking()
    }
}
