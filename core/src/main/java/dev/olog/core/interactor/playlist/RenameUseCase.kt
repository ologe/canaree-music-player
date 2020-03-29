package dev.olog.core.interactor.playlist

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import javax.inject.Inject

class RenameUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway

) {

    suspend operator fun invoke(mediaId: MediaId, newTitle: String) {
        return when (mediaId.category) {
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.renamePlaylist(
                mediaId.categoryId.toLong(),
                newTitle
            )
            MediaIdCategory.PLAYLISTS -> playlistGateway.renamePlaylist(
                mediaId.categoryId.toLong(),
                newTitle
            )
            else -> throw IllegalArgumentException("invalid media id $mediaId")
        }
    }
}