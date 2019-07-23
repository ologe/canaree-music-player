package dev.olog.core.interactor

import dev.olog.core.entity.PlaylistType
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import javax.inject.Inject

class InsertCustomTrackListToPlaylist @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway

) {

    operator fun invoke(param: InsertCustomTrackListRequest) {
        if (param.type == PlaylistType.PODCAST) {
            val playlistId = podcastPlaylistGateway.createPlaylist(param.playlistTitle)
            podcastPlaylistGateway.addSongsToPlaylist(playlistId, param.tracksId)
        } else {
            val playlistId = playlistGateway.createPlaylist(param.playlistTitle)
            playlistGateway.addSongsToPlaylist(playlistId, param.tracksId)
        }
    }
}

data class InsertCustomTrackListRequest(
    val playlistTitle: String,
    val tracksId: List<Long>,
    val type: PlaylistType
)