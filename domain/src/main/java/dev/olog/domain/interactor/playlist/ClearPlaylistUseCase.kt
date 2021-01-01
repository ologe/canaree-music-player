package dev.olog.domain.interactor.playlist

import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.PlaylistGateway
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