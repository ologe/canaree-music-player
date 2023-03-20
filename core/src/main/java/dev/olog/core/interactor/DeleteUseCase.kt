package dev.olog.core.interactor

import dev.olog.core.MediaId
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import javax.inject.Inject

class DeleteUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
) {

    suspend operator fun invoke(mediaId: MediaId) {
        return when {
            mediaId.isPodcastPlaylist -> podcastPlaylistGateway.deletePlaylist(mediaId.categoryId)
            mediaId.isPlaylist -> playlistGateway.deletePlaylist(mediaId.categoryId)
            else -> {}
        }
    }
}