package dev.olog.domain.interactor.search

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.RecentSearchesGateway
import dev.olog.domain.interactor.base.CompletableUseCase
import io.reactivex.Completable
import javax.inject.Inject

class ClearRecentSearchesUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val recentSearchesGateway: RecentSearchesGateway

) : CompletableUseCase(scheduler) {

    override fun buildUseCaseObservable(): Completable {
        return recentSearchesGateway.deleteAll()
    }
}