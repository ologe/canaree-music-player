package dev.olog.msc.domain.interactor.all.last.played

import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.ArtistGateway
import dev.olog.msc.domain.gateway.PodcastArtistGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import dev.olog.core.MediaId
import io.reactivex.Completable
import javax.inject.Inject

class InsertLastPlayedArtistUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val artistGateway: ArtistGateway,
        private val podcastGateway: PodcastArtistGateway

): CompletableUseCaseWithParam<MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Completable {
        if (mediaId.isPodcastArtist){
            return podcastGateway.addLastPlayed(mediaId.categoryValue.toLong())
        }
        return artistGateway.addLastPlayed(mediaId.categoryValue.toLong())
    }
}