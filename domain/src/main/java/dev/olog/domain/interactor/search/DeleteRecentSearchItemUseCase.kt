package dev.olog.domain.interactor.search

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.RecentSearchesGateway
import dev.olog.domain.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class DeleteRecentSearchItemUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val recentSearchesGateway: RecentSearchesGateway

) : CompletableUseCaseWithParam<Pair<Int, Long>>(scheduler) {

    override fun buildUseCaseObservable(param: Pair<Int, Long>): Completable {
        return recentSearchesGateway.deleteItem(param.first, param.second)
    }
}