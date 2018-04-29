package dev.olog.msc.domain.interactor.detail.sorting.library

import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.entity.SortArranging
import dev.olog.msc.domain.entity.SortType
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import dev.olog.msc.domain.interactor.tab.GetAllAlbumsUseCase
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

class GetAllAlbumsSortedUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val getAllUseCase: GetAllAlbumsUseCase,
        private val appPrefsGateway: AppPreferencesGateway

) : ObservableUseCase<List<Album>>(schedulers){

    override fun buildUseCaseObservable(): Observable<List<Album>> {
        return Observables.combineLatest(
                getAllUseCase.execute(),
                appPrefsGateway.observeAllAlbumsSortOrder(),
                { tracks, order ->
                    val (sort, arranging) = order

                    if (arranging == SortArranging.ASCENDING){
                        tracks.sortedWith(getAscendingComparator(sort))
                    } else {
                        tracks.sortedWith(getDescendingComparator(sort))
                    }
                })
    }

    private fun getAscendingComparator(sortType: SortType): Comparator<Album> {
        return when (sortType){
            SortType.TITLE -> compareBy { it.title.toLowerCase() }
            SortType.ARTIST -> compareBy { it.artist.toLowerCase() }
            else -> throw IllegalStateException("can't sort all albums, invalid sort type $sortType")
        }
    }

    private fun getDescendingComparator(sortType: SortType): Comparator<Album> {
        return when (sortType){
            SortType.TITLE -> compareByDescending { it.title.toLowerCase() }
            SortType.ARTIST -> compareByDescending { it.artist.toLowerCase() }
            else -> throw IllegalStateException("can't sort all albums, invalid sort type $sortType")
        }
    }

}