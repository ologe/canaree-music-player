package dev.olog.domain.interactor.detail.sorting

import dev.olog.domain.SortArranging
import dev.olog.domain.entity.Song
import dev.olog.domain.entity.SortType
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.shared.MediaId
import io.reactivex.Flowable
import io.reactivex.rxkotlin.Flowables
import java.util.*
import javax.inject.Inject

class GetSortedSongListByParamUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val getSortOrderUseCase: GetSortOrderUseCase,
        private val getSortArrangingUseCase: GetSortArrangingUseCase

) : FlowableUseCaseWithParam<List<Song>, MediaId>(schedulers){

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Flowable<List<Song>> {
        return Flowables.combineLatest(
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
            SortType.TITLE -> compareBy { it.title.toLowerCase() }
            SortType.ARTIST -> compareBy { it.artist.toLowerCase() }
            SortType.ALBUM -> compareBy { it.album.toLowerCase() }
            SortType.DURATION -> compareBy { it.duration }
            SortType.RECENTLY_ADDED -> compareByDescending { it.dateAdded }
            SortType.TRACK_NUMBER -> compareBy { it.trackNumber }
            SortType.CUSTOM -> compareBy { 0 }
        }
    }

    private fun getDescendingComparator(sortType: SortType): Comparator<Song> {
        return when (sortType){
            SortType.TITLE -> compareByDescending { it.title.toLowerCase() }
            SortType.ARTIST -> compareByDescending { it.artist.toLowerCase() }
            SortType.ALBUM -> compareByDescending { it.album.toLowerCase() }
            SortType.DURATION -> compareByDescending { it.duration }
            SortType.RECENTLY_ADDED -> compareBy { it.dateAdded }
            SortType.TRACK_NUMBER -> compareByDescending { it.trackNumber }
            SortType.CUSTOM -> compareByDescending { 0 }
        }
    }

}