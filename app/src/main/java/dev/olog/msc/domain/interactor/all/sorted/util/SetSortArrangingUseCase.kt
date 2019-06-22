package dev.olog.msc.domain.interactor.all.sorted.util

import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.core.prefs.SortPreferences
import dev.olog.msc.domain.interactor.base.CompletableUseCase
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