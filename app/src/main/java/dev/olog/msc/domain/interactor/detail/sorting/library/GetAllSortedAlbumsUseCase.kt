package dev.olog.msc.domain.interactor.detail.sorting.library

import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.entity.LibrarySortType
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import dev.olog.msc.domain.interactor.tab.GetAllAlbumsUseCase
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

class GetAllSortedAlbumsUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val getAllUseCase: GetAllAlbumsUseCase,
        private val sortOrder: GetAllAlbumsSortOrderUseCase

) : ObservableUseCase<List<Album>>(schedulers){

    override fun buildUseCaseObservable(): Observable<List<Album>> {
        return Observables.combineLatest(
                getAllUseCase.execute(),
                sortOrder.execute(), { albums, order ->
                    albums.sortedWith(getComparator(order))
                })
    }

    private fun getComparator(sortType: LibrarySortType): Comparator<Album> {
        return when (sortType){
            LibrarySortType.TITLE_AZ -> compareBy { it.title.toLowerCase() }
            LibrarySortType.TITLE_ZA -> compareByDescending { it.title.toLowerCase() }
            LibrarySortType.ARTIST_AZ -> compareBy { it.artist.toLowerCase() }
            LibrarySortType.ARTIST_ZA -> compareByDescending { it.artist.toLowerCase() }
            else -> throw IllegalStateException("invalid sortType=$sortType for albums")
        }
    }

}