package dev.olog.core.interactor.lastplayed

import dev.olog.core.mediaid.MediaId
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.core.gateway.podcast.PodcastArtistGateway
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