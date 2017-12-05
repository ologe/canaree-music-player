package dev.olog.domain.interactor.search

import dev.olog.domain.entity.SearchResult
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.RecentSearchesGateway
import dev.olog.domain.interactor.base.FlowableUseCase
import io.reactivex.Flowable
import javax.inject.Inject

class GetAllRecentSearchesUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val recentSearchesGateway: RecentSearchesGateway

) : FlowableUseCase<List<SearchResult>>(scheduler) {

    override fun buildUseCaseObservable(): Flowable<List<SearchResult>> {
        return recentSearchesGateway.getAll()
    }
}