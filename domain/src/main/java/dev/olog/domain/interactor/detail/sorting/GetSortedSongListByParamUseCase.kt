package dev.olog.domain.interactor.detail.sorting

import dev.olog.domain.SortArranging
import dev.olog.domain.entity.Song
import dev.olog.domain.entity.SortType
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import io.reactivex.Flowable
import io.reactivex.rxkotlin.Flowables
import javax.inject.Inject

class GetSortedSongListByParamUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val getSortOrderUseCase: GetSortOrderUseCase,
        private val getSortArrangingUseCase: GetSortArrangingUseCase

) : FlowableUseCaseWithParam<List<Song>, String>(schedulers){

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: String): Flowable<List<Song>> {
        return Flowables.combineLatest(
                getSongListByParamUseCase.execute(mediaId),
                getSortOrderUseCase.execute(mediaId),
                getSortArrangingUseCase.execute(), { songList, sortOrder, arranging ->
            val list = songList.sortedWith(getComparator(sortOrder))
            if (arranging == SortArranging.DESCENDING){
                list.asReversed()
            } else list
        })
    }

    private fun getComparator(sortType: SortType): Comparator<Song> {
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

}