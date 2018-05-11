package dev.olog.msc.domain.interactor.last.fm

import dev.olog.msc.app.IoSchedulers
import dev.olog.msc.domain.gateway.LastFmGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class DeleteLastFmTrackUseCase @Inject constructor(
        schedulers: IoSchedulers,
        private val gateway: LastFmGateway

): CompletableUseCaseWithParam<Long>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(trackId: Long): Completable {
        return Completable.fromCallable { gateway.deleteTrack(trackId) }
    }
}