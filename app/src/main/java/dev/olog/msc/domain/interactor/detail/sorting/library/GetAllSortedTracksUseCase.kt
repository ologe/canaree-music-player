package dev.olog.msc.domain.interactor.detail.sorting.library

import dev.olog.msc.domain.entity.LibrarySortType
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import dev.olog.msc.domain.interactor.tab.GetAllSongsUseCase
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

class GetAllSortedTracksUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val getAllUseCase: GetAllSongsUseCase,
        private val sortOrder: GetAllTracksSortOrderUseCase

) : ObservableUseCase<List<Song>>(schedulers){

    override fun buildUseCaseObservable(): Observable<List<Song>> {
        return Observables.combineLatest(
                getAllUseCase.execute(),
                sortOrder.execute(), { tracks, order ->
                    tracks.sortedWith(getComparator(order))
                })
    }

    private fun getComparator(sortType: LibrarySortType): Comparator<Song> {
        return when (sortType){
            LibrarySortType.TITLE_AZ -> compareBy { it.title.toLowerCase() }
            LibrarySortType.TITLE_ZA -> compareByDescending { it.title.toLowerCase() }
            LibrarySortType.ARTIST_AZ -> compareBy { it.artist.toLowerCase() }
            LibrarySortType.ARTIST_ZA -> compareByDescending { it.artist.toLowerCase() }
            LibrarySortType.ALBUM_AZ -> compareBy { it.album.toLowerCase() }
            LibrarySortType.ALBUM_ZA -> compareByDescending { it.album.toLowerCase() }
            LibrarySortType.DURATION_ASC -> compareBy { it.duration }
            LibrarySortType.DURATION_DESC -> compareByDescending { it.duration }
            LibrarySortType.RECENTLY_ADDED_ASC -> compareBy { it.dateAdded }
            LibrarySortType.RECENTLY_ADDED_DESC -> compareByDescending { it.dateAdded }
        }
    }

}