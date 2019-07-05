package dev.olog.msc.domain.interactor.all

import dev.olog.core.entity.track.Playlist
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.entity.PlaylistType
import javax.inject.Inject

class GetPlaylistsBlockingUseCase @Inject internal constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistgateway: PodcastPlaylistGateway

) {

    fun execute(type: PlaylistType): List<Playlist> {
        if (type == PlaylistType.PODCAST) {
            return podcastPlaylistgateway.getAll()
        }
        return playlistGateway.getAll()
    }
}
