package dev.olog.msc.domain.interactor

import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.domain.interactor.base.SingleUseCaseWithParam
import dev.olog.msc.utils.MediaId
import io.reactivex.Single
import javax.inject.Inject

class GetTotalSongDurationUseCase @Inject constructor(
        scheduler: ComputationScheduler,
        private val getSongListByParamUseCase: GetSongListByParamUseCase

): SingleUseCaseWithParam<Int, MediaId>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Single<Int> {
        return getSongListByParamUseCase.execute(mediaId)
                .firstOrError()
                .map { it.sumBy { it.duration.toInt() } }
    }
}