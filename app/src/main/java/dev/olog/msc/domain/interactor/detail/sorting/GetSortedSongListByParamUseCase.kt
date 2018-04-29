package dev.olog.msc.domain.interactor.detail.sorting

import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.entity.SortArranging
import dev.olog.msc.domain.entity.SortType
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.interactor.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.base.ObservableUseCaseUseCaseWithParam
import dev.olog.msc.utils.MediaId
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import java.text.Collator
import java.util.*
import javax.inject.Inject

class GetSortedSongListByParamUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val getSortOrderUseCase: GetSortOrderUseCase,
        private val getSortArrangingUseCase: GetSortArrangingUseCase,
        private val collator: Collator

) : ObservableUseCaseUseCaseWithParam<List<Song>, MediaId>(schedulers){

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<Song>> {
        return Observables.combineLatest(
                getSongListByParamUseCase.execute(mediaId),
                getSortOrderUseCase.execute(mediaId),
                getSortArrangingUseCase.execute(), { songList, sortOrder, arranging ->
            if (arranging == SortArranging.ASCENDING){
                songList.sortedWith(getAscendingComparator(sortOrder))
            } else {
                songList.sortedWith(getDescendingComparator(sortOrder))
            }
        })
    }

    private fun getAscendingComparator(sortType: SortType): Comparator<Song> {
        return when (sortType){
            SortType.TITLE -> Comparator { o1, o2 -> collator.compare(o1.title, o2.title) }
            SortType.ARTIST -> Comparator { o1, o2 -> collator.compare(o1.artist, o2.artist) }
            SortType.ALBUM -> Comparator { o1, o2 -> collator.compare(o1.album, o2.album) }
            SortType.DURATION -> compareBy { it.duration }
            SortType.RECENTLY_ADDED -> compareByDescending { it.dateAdded }
            SortType.TRACK_NUMBER -> compareBy { it.trackNumber }
            SortType.CUSTOM -> compareBy { 0 }
        }
    }

    private fun getDescendingComparator(sortType: SortType): Comparator<Song> {
        return when (sortType){
            SortType.TITLE -> Comparator { o1, o2 -> collator.compare(o2.title, o1.title) }
            SortType.ARTIST -> Comparator { o1, o2 -> collator.compare(o2.artist, o1.artist) }
            SortType.ALBUM -> Comparator { o1, o2 -> collator.compare(o2.album, o1.album) }
            SortType.DURATION -> compareByDescending { it.duration }
            SortType.RECENTLY_ADDED -> compareBy { it.dateAdded }
            SortType.TRACK_NUMBER -> compareByDescending { it.trackNumber }
            SortType.CUSTOM -> compareByDescending { 0 }
        }
    }

}