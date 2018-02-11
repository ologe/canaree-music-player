package dev.olog.msc.domain.interactor.search

import dev.olog.msc.domain.entity.SearchResult
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.RecentSearchesGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class GetAllRecentSearchesUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val recentSearchesGateway: RecentSearchesGateway

) : ObservableUseCase<List<SearchResult>>(scheduler) {

    override fun buildUseCaseObservable(): Observable<List<SearchResult>> {
        return recentSearchesGateway.getAll()
    }
}