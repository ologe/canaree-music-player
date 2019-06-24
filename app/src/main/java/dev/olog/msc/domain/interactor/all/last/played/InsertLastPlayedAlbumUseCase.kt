package dev.olog.msc.domain.interactor.all.last.played

import dev.olog.core.MediaId
import dev.olog.core.gateway.AlbumGateway2
import dev.olog.core.gateway.PodcastAlbumGateway2
import dev.olog.shared.assertBackgroundThread
import javax.inject.Inject

class InsertLastPlayedAlbumUseCase @Inject constructor(
        private val albumGateway: AlbumGateway2,
        private val podcastGateway: PodcastAlbumGateway2

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