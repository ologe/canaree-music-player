package dev.olog.domain.interactor.detail.sorting

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.domain.interactor.base.CompletableUseCase
import io.reactivex.Completable
import javax.inject.Inject

class SetSortArrangingUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val gateway: AppPreferencesGateway

) : CompletableUseCase(scheduler) {

    override fun buildUseCaseObservable(): Completable {
        return gateway.toggleSortArranging()
    }
}