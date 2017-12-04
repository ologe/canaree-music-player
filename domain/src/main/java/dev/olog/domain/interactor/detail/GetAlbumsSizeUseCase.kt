package dev.olog.domain.interactor.detail

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import io.reactivex.Flowable
import javax.inject.Inject

class GetAlbumsSizeUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val getSongListByParamUseCase: GetSongListByParamUseCase


) : FlowableUseCaseWithParam<Int, String>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: String): Flowable<Int> {
        return getSongListByParamUseCase.execute(mediaId)
                .map { it.size }
    }
}