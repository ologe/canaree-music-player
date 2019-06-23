package dev.olog.msc.domain.interactor.all.last.played

import dev.olog.core.MediaId
import dev.olog.core.gateway.AlbumGateway2
import dev.olog.msc.domain.gateway.PodcastAlbumGateway
import dev.olog.shared.assertBackgroundThread
import kotlinx.coroutines.rx2.await
import javax.inject.Inject

class InsertLastPlayedAlbumUseCase @Inject constructor(
    private val albumGateway: AlbumGateway2,
    private val podcastGateway: PodcastAlbumGateway

) {

    suspend operator fun invoke(mediaId: MediaId){
        assertBackgroundThread()
        if (mediaId.isPodcastAlbum){
            podcastGateway.addLastPlayed(mediaId.categoryValue.toLong()).await()
        } else {
            albumGateway.addLastPlayed(mediaId.categoryValue.toLong())
        }
    }

}