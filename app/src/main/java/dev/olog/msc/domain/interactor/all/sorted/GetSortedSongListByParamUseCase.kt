package dev.olog.msc.domain.interactor.all.sorted

import dev.olog.core.entity.track.Song
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortType
import dev.olog.core.executor.IoScheduler
import dev.olog.msc.domain.interactor.all.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.all.sorted.util.GetSortArrangingUseCase
import dev.olog.msc.domain.interactor.all.sorted.util.GetSortOrderUseCase
import dev.olog.msc.domain.interactor.base.ObservableUseCaseWithParam
import dev.olog.msc.utils.ComparatorUtils
import dev.olog.core.MediaId
import dev.olog.msc.utils.safeCompare
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import java.text.Collator
import javax.inject.Inject

class GetSortedSongListByParamUseCase @Inject constructor(
    schedulers: IoScheduler,
    private val getSongListByParamUseCase: GetSongListByParamUseCase,
    private val getSortOrderUseCase: GetSortOrderUseCase,
    private val getSortArrangingUseCase: GetSortArrangingUseCase,
    private val collator: Collator

) : ObservableUseCaseWithParam<List<Song>, MediaId>(schedulers){

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
            SortType.TITLE -> Comparator { o1, o2 -> collator.safeCompare(o1.title, o2.title) }
            SortType.ARTIST -> Comparator { o1, o2 -> collator.safeCompare(o1.artist, o2.artist) }
            SortType.ALBUM -> Comparator { o1, o2 -> collator.safeCompare(o1.album, o2.album) }
            SortType.ALBUM_ARTIST -> Comparator { o1, o2 -> collator.safeCompare(o1.albumArtist, o2.albumArtist) }
            SortType.DURATION -> compareBy { it.duration }
            SortType.RECENTLY_ADDED -> compareByDescending { it.dateAdded }
            SortType.TRACK_NUMBER -> ComparatorUtils.getAscendingTrackNumberComparator()
            SortType.CUSTOM -> compareBy { 0 }
        }
    }

    private fun getDescendingComparator(sortType: SortType): Comparator<Song> {
        return when (sortType){
            SortType.TITLE -> Comparator { o1, o2 -> collator.safeCompare(o2.title, o1.title) }
            SortType.ARTIST -> Comparator { o1, o2 -> collator.safeCompare(o2.artist, o1.artist) }
            SortType.ALBUM -> Comparator { o1, o2 -> collator.safeCompare(o2.album, o1.album) }
            SortType.ALBUM_ARTIST -> Comparator { o1, o2 -> collator.safeCompare(o2.albumArtist, o1.albumArtist) }
            SortType.DURATION -> compareByDescending { it.duration }
            SortType.RECENTLY_ADDED -> compareBy { it.dateAdded }
            SortType.TRACK_NUMBER -> ComparatorUtils.getDescendingTrackNumberComparator()
            SortType.CUSTOM -> compareByDescending { 0 }
        }
    }



}