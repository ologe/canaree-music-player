package dev.olog.msc.domain.interactor.all

import dev.olog.core.entity.track.Playlist
import dev.olog.presentation.model.PlaylistType
import dev.olog.msc.domain.gateway.PlaylistGateway
import dev.olog.msc.domain.gateway.PodcastPlaylistGateway
import javax.inject.Inject

class GetPlaylistsBlockingUseCase @Inject internal constructor(
        private val playlistGateway: PlaylistGateway,
        private val podcastPlaylistgateway: PodcastPlaylistGateway

)  {

    fun execute(type: PlaylistType): List<Playlist>{
        if (type == PlaylistType.PODCAST){
            return podcastPlaylistgateway.getPlaylistsBlocking()
                    .map { Playlist(it.id, it.title, it.size, true) }
        }
        return playlistGateway.getPlaylistsBlocking()
    }
}
