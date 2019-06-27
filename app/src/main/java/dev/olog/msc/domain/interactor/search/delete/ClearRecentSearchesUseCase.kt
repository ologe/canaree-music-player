package dev.olog.msc.domain.interactor.search.delete

import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.RecentSearchesGateway
import dev.olog.core.interactor.base.CompletableUseCase
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