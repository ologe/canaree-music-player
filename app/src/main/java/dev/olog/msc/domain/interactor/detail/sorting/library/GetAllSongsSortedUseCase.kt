package dev.olog.msc.domain.interactor.detail.sorting.library

import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.entity.SortArranging
import dev.olog.msc.domain.entity.SortType
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import dev.olog.msc.domain.interactor.tab.GetAllSongsUseCase
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

class GetAllSongsSortedUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val getAllUseCase: GetAllSongsUseCase,
        private val appPrefsGateway: AppPreferencesGateway


) : ObservableUseCase<List<Song>>(schedulers){

    override fun buildUseCaseObservable(): Observable<List<Song>> {
        return Observables.combineLatest(
                getAllUseCase.execute(),
                appPrefsGateway.observeAllTracksSortOrder(),
                { tracks, order ->
                    val (sort, arranging) = order

                    if (arranging == SortArranging.ASCENDING){
                        tracks.sortedWith(getAscendingComparator(sort))
                    } else {
                        tracks.sortedWith(getDescendingComparator(sort))
                    }
                })
    }

    private fun getAscendingComparator(sortType: SortType): Comparator<Song> {
        return when (sortType){
            SortType.TITLE -> compareBy { it.title.toLowerCase() }
            SortType.ARTIST -> compareBy { it.artist.toLowerCase() }
            SortType.ALBUM -> compareBy { it.album.toLowerCase() }
            SortType.DURATION -> compareBy { it.duration }
            SortType.RECENTLY_ADDED -> compareBy { it.dateAdded }
            else -> throw IllegalStateException("can't sort all tracks, invalid sort type $sortType")
        }
    }

    private fun getDescendingComparator(sortType: SortType): Comparator<Song> {
        return when (sortType){
            SortType.TITLE -> compareByDescending { it.title.toLowerCase() }
            SortType.ARTIST -> compareByDescending { it.artist.toLowerCase() }
            SortType.ALBUM -> compareByDescending { it.album.toLowerCase() }
            SortType.DURATION -> compareByDescending { it.duration }
            SortType.RECENTLY_ADDED -> compareByDescending { it.dateAdded }
            else -> throw IllegalStateException("can't sort all tracks, invalid sort type $sortType")
        }
    }

}