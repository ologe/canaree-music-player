package dev.olog.domain.interactor.detail.sorting

import dev.olog.domain.entity.SortArranging
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.domain.interactor.base.FlowableUseCase
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