package dev.olog.domain.interactor.detail.recent

import dev.olog.domain.entity.Song
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GetRecentlyAddedUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val getSongListByParamUseCase: GetSongListByParamUseCase

) : FlowableUseCaseWithParam<List<Song>, String>(scheduler) {

    companion object {
        private val ONE_WEEK = TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS)
    }

    override fun buildUseCaseObservable(param: String): Flowable<List<Song>> {
        val category = MediaIdHelper.extractCategory(param)
        if (category == MediaIdHelper.MEDIA_ID_BY_PLAYLIST){
            return Flowable.just(listOf())
        }


        return getSongListByParamUseCase.execute(param)
                .map { if (it.size >= 5) it else listOf() }
                .flatMapSingle { it.toFlowable()
                        .filter { (System.currentTimeMillis() - it.dateAdded * 1000) <= ONE_WEEK }
                        .toList()
                }
    }
}