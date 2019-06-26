package dev.olog.msc.domain.interactor.all.last.played

import dev.olog.core.MediaId
import dev.olog.core.gateway.ArtistGateway
import dev.olog.core.gateway.PodcastArtistGateway
import dev.olog.shared.utils.assertBackgroundThread
import javax.inject.Inject

class InsertLastPlayedArtistUseCase @Inject constructor(
    private val artistGateway: ArtistGateway,
    private val podcastGateway: PodcastArtistGateway

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