package dev.olog.domain.interactor.lastplayed

import dev.olog.domain.gateway.podcast.PodcastArtistGateway
import dev.olog.domain.gateway.track.ArtistGateway
import dev.olog.domain.mediaid.MediaId
import javax.inject.Inject

class InsertLastPlayedArtistUseCase @Inject constructor(
    private val artistGateway: ArtistGateway,
    private val podcastArtistGateway: PodcastArtistGateway

) {

    suspend operator fun invoke(mediaId: MediaId) {
        if (mediaId.isPodcastArtist) {
            return podcastArtistGateway.addLastPlayed(mediaId.categoryValue.toLong())
        }
        return artistGateway.addLastPlayed(mediaId.categoryValue.toLong())
    }
}