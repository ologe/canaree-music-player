package dev.olog.domain.interactor.lastplayed

import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.gateway.track.AlbumGateway
import dev.olog.domain.gateway.podcast.PodcastAlbumGateway
import javax.inject.Inject

class InsertLastPlayedAlbumUseCase @Inject constructor(
    private val albumGateway: AlbumGateway,
    private val podcastGateway: PodcastAlbumGateway

) {

    suspend operator fun invoke(mediaId: MediaId) {
        if (mediaId.isPodcastAlbum) {
            podcastGateway.addLastPlayed(mediaId.categoryValue.toLong())
        } else {
            albumGateway.addLastPlayed(mediaId.categoryValue.toLong())
        }
    }

}