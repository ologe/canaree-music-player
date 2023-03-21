package dev.olog.core.interactor.lastplayed

import dev.olog.core.MediaId
import dev.olog.core.gateway.track.AlbumGateway
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import javax.inject.Inject

class InsertLastPlayedAlbumUseCase @Inject constructor(
    private val albumGateway: AlbumGateway,
    private val podcastGateway: PodcastAlbumGateway

) {

    suspend operator fun invoke(mediaId: MediaId) {
        if (mediaId.isPodcastAlbum) {
            podcastGateway.addRecentlyPlayed(mediaId.categoryValue.toLong())
        } else {
            albumGateway.addRecentlyPlayed(mediaId.categoryValue.toLong())
        }
    }

}