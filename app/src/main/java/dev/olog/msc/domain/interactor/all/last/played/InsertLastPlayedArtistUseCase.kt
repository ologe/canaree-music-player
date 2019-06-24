package dev.olog.msc.domain.interactor.all.last.played

import dev.olog.core.MediaId
import dev.olog.core.gateway.ArtistGateway2
import dev.olog.core.gateway.PodcastArtistGateway2
import dev.olog.shared.assertBackgroundThread
import javax.inject.Inject

class InsertLastPlayedArtistUseCase @Inject constructor(
        private val artistGateway: ArtistGateway2,
        private val podcastGateway: PodcastArtistGateway2

) {

    suspend operator fun invoke(mediaId: MediaId) {
        assertBackgroundThread()
        if (mediaId.isPodcastArtist) {
            podcastGateway.addLastPlayed(mediaId.categoryValue.toLong())
        } else {
            artistGateway.addLastPlayed(mediaId.categoryValue.toLong())
        }
    }
}