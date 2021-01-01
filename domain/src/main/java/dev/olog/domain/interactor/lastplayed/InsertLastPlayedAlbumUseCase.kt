package dev.olog.domain.interactor.lastplayed

import dev.olog.domain.gateway.podcast.PodcastAlbumGateway
import dev.olog.domain.gateway.track.AlbumGateway
import dev.olog.domain.mediaid.MediaId
import javax.inject.Inject

class InsertLastPlayedAlbumUseCase @Inject constructor(
    private val albumGateway: AlbumGateway,
    private val podcastAlbumGateway: PodcastAlbumGateway
) {

    suspend operator fun invoke(mediaId: MediaId) {
        if (mediaId.isPodcastAlbum) {
            return podcastAlbumGateway.addLastPlayed(mediaId.categoryValue.toLong())
        }
        return albumGateway.addLastPlayed(mediaId.categoryValue.toLong())
    }

}