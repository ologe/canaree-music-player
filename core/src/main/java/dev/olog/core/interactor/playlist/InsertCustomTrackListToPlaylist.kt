package dev.olog.core.interactor.playlist

import dev.olog.core.entity.PlaylistType
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import javax.inject.Inject

class InsertCustomTrackListToPlaylist @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway

) {

    suspend operator fun invoke(param: InsertCustomTrackListRequest) {
        if (param.type == PlaylistType.PODCAST) {
            val playlistId = podcastPlaylistGateway.createPlaylist(param.playlistTitle)
            podcastPlaylistGateway.addSongsToPlaylist(playlistId, param.tracksId)
        } else {
            val playlistId = playlistGateway.createPlaylist(param.playlistTitle)
            playlistGateway.addSongsToPlaylist(playlistId, param.tracksId)
        }
    }
}

class InsertCustomTrackListRequest(
    @JvmField
    val playlistTitle: String,
    @JvmField
    val tracksId: List<Long>,
    @JvmField
    val type: PlaylistType
)