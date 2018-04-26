package dev.olog.msc.domain.interactor.detail.sorting.library

import dev.olog.msc.domain.entity.Artist
import dev.olog.msc.domain.entity.LibrarySortType
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import dev.olog.msc.domain.interactor.tab.GetAllArtistsUseCase
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

class GetAllSortedArtistsUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val getAllUseCase: GetAllArtistsUseCase,
        private val sortOrder: GetAllArtistsSortOrderUseCase

) : ObservableUseCase<List<Artist>>(schedulers){

    override fun buildUseCaseObservable(): Observable<List<Artist>> {
        return Observables.combineLatest(
                getAllUseCase.execute(),
                sortOrder.execute(), { artists, order ->
                    artists.sortedWith(getComparator(order))
                })
    }

    private fun getComparator(sortType: LibrarySortType): Comparator<Artist> {
        return when (sortType){
            LibrarySortType.ARTIST_AZ -> compareBy { it.name.toLowerCase() }
            LibrarySortType.ALBUM_ZA -> compareByDescending { it.name.toLowerCase() }
            else -> throw IllegalStateException("invalid sortType=$sortType for artists")
        }
    }

}