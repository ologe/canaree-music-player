package dev.olog.core.interactor.playlist

import dev.olog.core.MediaId
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import javax.inject.Inject

class ClearPlaylistUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway

) {

    suspend operator fun invoke(mediaId: MediaId) {
        val playlistId = mediaId.resolveId
        if (mediaId.isPodcastPlaylist) {
            return podcastPlaylistGateway.clearPlaylist(playlistId)
        }
        return playlistGateway.clearPlaylist(playlistId)
    }
}