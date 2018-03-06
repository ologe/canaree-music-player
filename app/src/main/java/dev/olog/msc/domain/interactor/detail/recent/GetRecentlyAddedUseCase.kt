package dev.olog.msc.domain.interactor.detail.recent

import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.interactor.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.base.ObservableUseCaseUseCaseWithParam
import dev.olog.msc.utils.MediaId
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GetRecentlyAddedUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val getSongListByParamUseCase: GetSongListByParamUseCase

) : ObservableUseCaseUseCaseWithParam<List<Song>, MediaId>(scheduler) {

    companion object {
        private val TWO_WEEKS = TimeUnit.MILLISECONDS.convert(14, TimeUnit.DAYS)
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<Song>> {
        if (mediaId.isFolder || mediaId.isGenre){
            return getSongListByParamUseCase.execute(mediaId)
                    .map { if (it.size >= 5) it else listOf() }
                    .map { it.filter { (System.currentTimeMillis() - it.dateAdded * 1000) <= TWO_WEEKS } }
        }
        return Observable.just(listOf())
    }
}