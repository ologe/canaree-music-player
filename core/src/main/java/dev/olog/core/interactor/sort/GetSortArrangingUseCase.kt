package dev.olog.core.interactor.sort

import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.executor.IoScheduler
import dev.olog.core.interactor.base.ObservableUseCase
import dev.olog.core.prefs.SortPreferences
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