package dev.olog.msc.domain.interactor.last.fm

import dev.olog.msc.api.last.fm.model.SearchedTrack
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.LastFmCacheGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class InsertLastFmTrackUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: LastFmCacheGateway

) : CompletableUseCaseWithParam<SearchedTrack>(schedulers) {

    override fun buildUseCaseObservable(param: SearchedTrack): Completable {
        return gateway.insertTrack(param)
    }
}