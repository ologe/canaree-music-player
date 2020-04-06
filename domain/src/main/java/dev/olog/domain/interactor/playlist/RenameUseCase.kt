package dev.olog.domain.interactor.playlist

import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.PlaylistGateway
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