package dev.olog.domain.interactor.playlist

import dev.olog.domain.entity.PlaylistType
import dev.olog.domain.entity.track.Playlist
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.PlaylistGateway
import javax.inject.Inject

class GetPlaylistsUseCase @Inject internal constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistgateway: PodcastPlaylistGateway
) {

    suspend fun execute(type: PlaylistType): List<Playlist> {
        return when (type) {
            PlaylistType.TRACK -> playlistGateway.getAll()
            PlaylistType.PODCAST -> podcastPlaylistgateway.getAll()
            PlaylistType.AUTO -> error("invalid type=$type")
        }
    }
}
