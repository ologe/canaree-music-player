package dev.olog.core.interactor.playlist

import dev.olog.core.mediaid.MediaId
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import javax.inject.Inject

class RenameUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway

) {

    suspend operator fun invoke(mediaId: MediaId, newTitle: String) {
        return when {
            mediaId.isPodcastPlaylist -> podcastPlaylistGateway.renamePlaylist(
                mediaId.categoryValue.toLong(),
                newTitle
            )
            mediaId.isPlaylist -> playlistGateway.renamePlaylist(
                mediaId.categoryValue.toLong(),
                newTitle
            )
            else -> throw IllegalArgumentException("not a folder nor a playlist, $mediaId")
        }
    }
}