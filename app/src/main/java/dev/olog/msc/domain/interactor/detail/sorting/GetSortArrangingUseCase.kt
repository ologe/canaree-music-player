package dev.olog.msc.domain.interactor.detail.sorting

import dev.olog.msc.domain.entity.SortArranging
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.domain.interactor.base.FlowableUseCase
import io.reactivex.Flowable
import javax.inject.Inject

class GetSortArrangingUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val gateway: AppPreferencesGateway

) : FlowableUseCase<SortArranging>(scheduler) {

    override fun buildUseCaseObservable(): Flowable<SortArranging> {
        return gateway.getSortArranging()
    }
}