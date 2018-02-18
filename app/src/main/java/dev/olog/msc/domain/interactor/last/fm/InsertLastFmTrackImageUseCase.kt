package dev.olog.msc.domain.interactor.last.fm

import dev.olog.msc.api.last.fm.model.SearchedImage
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.LastFmCacheGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class InsertLastFmTrackImageUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: LastFmCacheGateway

) : CompletableUseCaseWithParam<SearchedImage>(schedulers) {

    override fun buildUseCaseObservable(param: SearchedImage): Completable {
        return gateway.insertTrackImage(param)
    }
}