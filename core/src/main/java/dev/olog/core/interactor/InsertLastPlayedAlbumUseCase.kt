package dev.olog.core.interactor

import dev.olog.core.MediaId
import dev.olog.core.gateway.track.AlbumGateway
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.shared.utils.assertBackgroundThread
import javax.inject.Inject

class InsertLastPlayedAlbumUseCase @Inject constructor(
    private val albumGateway: AlbumGateway,
    private val podcastGateway: PodcastAlbumGateway

) {

    suspend operator fun invoke(mediaId: MediaId) {
        assertBackgroundThread()
        if (mediaId.isPodcastAlbum) {
            podcastGateway.addLastPlayed(mediaId.categoryValue.toLong())
        } else {
            albumGateway.addLastPlayed(mediaId.categoryValue.toLong())
        }
    }

}