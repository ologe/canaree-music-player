package dev.olog.msc.domain.interactor.item

import dev.olog.core.entity.podcast.PodcastArtist
import dev.olog.core.executor.IoScheduler
import dev.olog.msc.domain.gateway.PodcastArtistGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCaseWithParam
import dev.olog.core.MediaId
import io.reactivex.Observable
import javax.inject.Inject

class GetPodcastArtistUseCase @Inject internal constructor(
    schedulers: IoScheduler,
    private val gateway: PodcastArtistGateway

) : ObservableUseCaseWithParam<PodcastArtist, MediaId>(schedulers) {

    override fun buildUseCaseObservable(param: MediaId): Observable<PodcastArtist> {
        return gateway.getByParam(param.categoryId)
    }
}