package dev.olog.msc.domain.interactor.item

import dev.olog.msc.domain.entity.PodcastArtist
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.PodcastArtistGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCaseUseCaseWithParam
import dev.olog.msc.utils.MediaId
import io.reactivex.Observable
import javax.inject.Inject

class GetPodcastArtistUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: PodcastArtistGateway

) : ObservableUseCaseUseCaseWithParam<PodcastArtist, MediaId>(schedulers) {

    override fun buildUseCaseObservable(param: MediaId): Observable<PodcastArtist> {
        return gateway.getByParam(param.resolveId)
    }
}