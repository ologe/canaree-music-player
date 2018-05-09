package dev.olog.msc.domain.interactor.all.sorted.util

import dev.olog.msc.domain.entity.SortArranging
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class GetSortArrangingUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val gateway: AppPreferencesGateway

) : ObservableUseCase<SortArranging>(scheduler) {

    override fun buildUseCaseObservable(): Observable<SortArranging> {
        return gateway.getSortArranging()
    }
}