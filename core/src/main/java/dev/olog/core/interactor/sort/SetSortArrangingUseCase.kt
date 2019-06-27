package dev.olog.core.interactor.sort

import dev.olog.core.executor.IoScheduler
import dev.olog.core.interactor.base.CompletableUseCase
import dev.olog.core.prefs.SortPreferences
import io.reactivex.Completable
import javax.inject.Inject

class SetSortArrangingUseCase @Inject constructor(
    scheduler: IoScheduler,
    private val gateway: SortPreferences

) : CompletableUseCase(scheduler) {

    override fun buildUseCaseObservable(): Completable {
        return gateway.toggleSortArranging()
    }
}