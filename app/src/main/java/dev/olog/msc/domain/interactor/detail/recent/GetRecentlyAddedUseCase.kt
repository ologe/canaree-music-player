package dev.olog.msc.domain.interactor.detail.recent

import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.interactor.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.msc.utils.MediaId
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GetRecentlyAddedUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val getSongListByParamUseCase: GetSongListByParamUseCase

) : FlowableUseCaseWithParam<List<Song>, MediaId>(scheduler) {

    companion object {
        private val TWO_WEEKS = TimeUnit.MILLISECONDS.convert(14, TimeUnit.DAYS)
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Flowable<List<Song>> {
        if (mediaId.isPlaylist){
            return Flowable.just(listOf())
        }

        return getSongListByParamUseCase.execute(mediaId)
                .map { if (it.size >= 5) it else listOf() }
                .flatMapSingle { it.toFlowable()
                        .filter { (System.currentTimeMillis() - it.dateAdded * 1000) <= TWO_WEEKS }
                        .toList()
                }
    }
}