package dev.olog.core.interactor.lastplayed

import dev.olog.core.MediaId
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.track.ArtistGateway
import javax.inject.Inject

class InsertLastPlayedArtistUseCase @Inject constructor(
    private val artistGateway: ArtistGateway,
    private val podcastGateway: PodcastArtistGateway

) {

    suspend operator fun invoke(mediaId: MediaId) {
        if (mediaId.isPodcastArtist) {
            podcastGateway.addRecentlyPlayed(mediaId.categoryId)
        } else {
            artistGateway.addRecentlyPlayed(mediaId.categoryId)
        }
    }
}