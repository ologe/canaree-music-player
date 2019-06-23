package dev.olog.msc.domain.interactor.all.last.played

import dev.olog.core.MediaId
import dev.olog.core.gateway.ArtistGateway2
import dev.olog.msc.domain.gateway.PodcastArtistGateway
import dev.olog.shared.assertBackgroundThread
import kotlinx.coroutines.rx2.await
import javax.inject.Inject

class InsertLastPlayedArtistUseCase @Inject constructor(
    private val artistGateway: ArtistGateway2,
    private val podcastGateway: PodcastArtistGateway

) {

    suspend operator fun invoke(mediaId: MediaId) {
        assertBackgroundThread()
        if (mediaId.isPodcastArtist) {
            podcastGateway.addLastPlayed(mediaId.categoryValue.toLong()).await()
        } else {
            artistGateway.addLastPlayed(mediaId.categoryValue.toLong())
        }
    }
}