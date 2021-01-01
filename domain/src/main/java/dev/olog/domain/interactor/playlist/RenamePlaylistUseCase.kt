package dev.olog.domain.interactor.playlist

import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.PlaylistGateway
import dev.olog.domain.mediaid.MediaId
import javax.inject.Inject

class RenamePlaylistUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway
) {

    suspend operator fun invoke(
        mediaId: MediaId,
        newTitle: String
    ) {
        if (mediaId.isPodcastPlaylist) {
            return podcastPlaylistGateway.renamePlaylist(
                playlistId = mediaId.categoryValue.toLong(),
                newTitle = newTitle
            )
        }
        if (mediaId.isPlaylist) {
            return playlistGateway.renamePlaylist(
                playlistId = mediaId.categoryValue.toLong(),
                newTitle = newTitle
            )
        }
        error("invalid mediaid=$mediaId")
    }
}