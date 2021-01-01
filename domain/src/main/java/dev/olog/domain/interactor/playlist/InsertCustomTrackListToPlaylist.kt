package dev.olog.domain.interactor.playlist

import dev.olog.domain.entity.PlaylistType
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.PlaylistGateway
import javax.inject.Inject

class InsertCustomTrackListToPlaylist @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway

) {

    suspend operator fun invoke(
        playlistTitle: String,
        tracksId: List<Long>,
        type: PlaylistType
    ) {

        if (type == PlaylistType.PODCAST) {
            val playlistId = podcastPlaylistGateway.createPlaylist(playlistTitle)

            podcastPlaylistGateway.addSongsToPlaylist(playlistId, tracksId)
        } else {
            val playlistId = playlistGateway.createPlaylist(playlistTitle)

            playlistGateway.addSongsToPlaylist(playlistId, tracksId)
        }
    }
}