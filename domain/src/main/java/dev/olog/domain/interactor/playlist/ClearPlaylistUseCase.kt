package dev.olog.domain.interactor.playlist

import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.PlaylistGateway
import dev.olog.domain.mediaid.MediaId
import javax.inject.Inject

class ClearPlaylistUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway
) {

    suspend operator fun invoke(mediaId: MediaId.Category) {
        if (mediaId.isPodcastPlaylist) {
            return podcastPlaylistGateway.clearPlaylist(mediaId.categoryValue.toLong())
        }
        return playlistGateway.clearPlaylist(mediaId.categoryValue.toLong())
    }
}