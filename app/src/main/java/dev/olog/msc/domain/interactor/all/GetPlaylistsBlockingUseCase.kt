package dev.olog.msc.domain.interactor.all

import dev.olog.core.entity.track.Playlist
import dev.olog.core.gateway.PlaylistGateway2
import dev.olog.core.gateway.PodcastPlaylistGateway2
import dev.olog.presentation.model.PlaylistType
import javax.inject.Inject

class GetPlaylistsBlockingUseCase @Inject internal constructor(
        private val playlistGateway: PlaylistGateway2,
        private val podcastPlaylistgateway: PodcastPlaylistGateway2

) {

    fun execute(type: PlaylistType): List<Playlist> {
        if (type == PlaylistType.PODCAST) {
            return podcastPlaylistgateway.getAll()
        }
        return playlistGateway.getAll()
    }
}
