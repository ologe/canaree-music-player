package dev.olog.msc.domain.interactor.all.sorted.util

import dev.olog.core.entity.SortArranging
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.core.prefs.SortPreferences
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class GetSortArrangingUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val gateway: SortPreferences

) : ObservableUseCase<SortArranging>(scheduler) {

    override fun buildUseCaseObservable(): Observable<SortArranging> {
        return gateway.observeDetailSortArranging()
    }
}