package dev.olog.msc.domain.interactor.all

import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.domain.entity.PlaylistType
import dev.olog.msc.domain.gateway.PlaylistGateway
import javax.inject.Inject

class GetPlaylistsBlockingUseCase @Inject internal constructor(
        private val gateway: PlaylistGateway

)  {

    fun execute(type: PlaylistType): List<Playlist>{
        if (type == PlaylistType.PODCAST){
            return gateway.getPlaylistsPodcastBlocking()
        }
        return gateway.getPlaylistsBlocking()
    }
}
