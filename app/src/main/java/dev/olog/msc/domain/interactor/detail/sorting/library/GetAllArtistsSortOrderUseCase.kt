package dev.olog.msc.domain.interactor.detail.sorting.library

import dev.olog.msc.domain.entity.LibrarySortType
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class GetAllArtistsSortOrderUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: AppPreferencesGateway

) : ObservableUseCase<LibrarySortType>(schedulers) {

    override fun buildUseCaseObservable(): Observable<LibrarySortType> {
        return gateway.getAllArtistSortOrder()
    }
}