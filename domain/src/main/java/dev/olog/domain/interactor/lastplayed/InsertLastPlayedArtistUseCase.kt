package dev.olog.domain.interactor.lastplayed

import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.gateway.track.ArtistGateway
import dev.olog.domain.gateway.podcast.PodcastArtistGateway
import javax.inject.Inject

class InsertLastPlayedArtistUseCase @Inject constructor(
    private val artistGateway: ArtistGateway,
    private val podcastGateway: PodcastArtistGateway

) {

    suspend operator fun invoke(mediaId: MediaId) {
        if (mediaId.isPodcastArtist) {
            podcastGateway.addLastPlayed(mediaId.categoryValue.toLong())
        } else {
            artistGateway.addLastPlayed(mediaId.categoryValue.toLong())
        }
    }
}