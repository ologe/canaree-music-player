package dev.olog.core.interactor.playlist

import dev.olog.core.MediaId.Category
import dev.olog.core.MediaIdCategory.PLAYLISTS
import dev.olog.core.MediaIdCategory.PODCASTS_PLAYLIST
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import javax.inject.Inject

class RemoveDuplicatesUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway

) {

    suspend operator fun invoke(mediaId: Category) {
        return when (mediaId.category){
            PODCASTS_PLAYLIST -> podcastPlaylistGateway.removeDuplicated(mediaId.categoryId)
            PLAYLISTS -> playlistGateway.removeDuplicated(mediaId.categoryId)
            else -> throw IllegalArgumentException("invalid media id $mediaId")
        }
    }
}