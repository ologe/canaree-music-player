package dev.olog.domain.interactor.playlist

import dev.olog.domain.MediaId.Category
import dev.olog.domain.MediaIdCategory.PLAYLISTS
import dev.olog.domain.MediaIdCategory.PODCASTS_PLAYLIST
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.PlaylistGateway
import javax.inject.Inject

class RemoveDuplicatesUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway

) {

    suspend operator fun invoke(mediaId: Category) {
        return when (mediaId.category){
            PODCASTS_PLAYLIST -> podcastPlaylistGateway.removeDuplicated(mediaId.categoryId.toLong())
            PLAYLISTS -> playlistGateway.removeDuplicated(mediaId.categoryId.toLong())
            else -> throw IllegalArgumentException("invalid media id $mediaId")
        }
    }
}