package dev.olog.msc.domain.interactor.item

import dev.olog.msc.domain.entity.Podcast
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.PodcastGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCaseWithParam
import dev.olog.msc.utils.MediaId
import io.reactivex.Observable
import javax.inject.Inject

class GetPodcastUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: PodcastGateway

) : ObservableUseCaseWithParam<Podcast, MediaId>(schedulers) {

    override fun buildUseCaseObservable(param: MediaId): Observable<Podcast> {
        return gateway.getByParam(param.resolveId)
    }
}
