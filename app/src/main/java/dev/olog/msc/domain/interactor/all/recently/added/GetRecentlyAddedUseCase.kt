package dev.olog.msc.domain.interactor.all.recently.added

import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.interactor.all.GetAllSongsUseCase
import dev.olog.msc.domain.interactor.all.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.base.ObservableUseCaseUseCaseWithParam
import dev.olog.msc.utils.MediaId
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private val TWO_WEEKS = TimeUnit.MILLISECONDS.convert(14, TimeUnit.DAYS)

internal fun getRecentlyAddedSong(getAllSongsUseCase: GetAllSongsUseCase)
        : Observable<List<Song>>{

    val time = System.currentTimeMillis()
    return getAllSongsUseCase.execute()
            .map { songList ->
                songList.filter { (time - it.dateAdded * 1000) <= TWO_WEEKS }
            }
}

class GetRecentlyAddedUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val getSongListByParamUseCase: GetSongListByParamUseCase

) : ObservableUseCaseUseCaseWithParam<List<Song>, MediaId>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<Song>> {
        val time = System.currentTimeMillis()
        if (mediaId.isFolder || mediaId.isGenre){
            return getSongListByParamUseCase.execute(mediaId)
                    .map { if (it.size >= 5) it else listOf() }
                    .map { songList -> songList.filter { (time - it.dateAdded * 1000) <= TWO_WEEKS } }
        }
        return Observable.just(listOf())
    }
}