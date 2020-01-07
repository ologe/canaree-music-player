package dev.olog.core.interactor.playlist

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import javax.inject.Inject

class RemoveDuplicatesUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway

) {

    suspend operator fun invoke(mediaId: MediaId) {
        val playlistId = mediaId.resolveId
        return when (mediaId.category){
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.removeDuplicated(playlistId)
            MediaIdCategory.PLAYLISTS -> playlistGateway.removeDuplicated(playlistId)
            else -> throw IllegalArgumentException("invalid media id $mediaId")

        }
    }
}