package dev.olog.core.interactor.playlist

import dev.olog.core.entity.PlaylistType
import dev.olog.core.entity.track.Playlist
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import javax.inject.Inject

class GetPlaylistsUseCase @Inject internal constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistgateway: PodcastPlaylistGateway

) {

    operator fun invoke(type: PlaylistType): List<Playlist> {
        return when (type) {
            PlaylistType.PODCAST -> podcastPlaylistgateway.getAll()
            PlaylistType.TRACK -> playlistGateway.getAll()
        }
    }
}
