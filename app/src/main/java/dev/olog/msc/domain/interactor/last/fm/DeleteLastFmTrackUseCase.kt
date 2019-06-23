package dev.olog.msc.domain.interactor.last.fm

import dev.olog.msc.app.IoSchedulers
import dev.olog.msc.domain.gateway.LastFmGateway
import dev.olog.core.interactor.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class DeleteLastFmTrackUseCase @Inject constructor(
        schedulers: IoSchedulers,
        private val gateway: LastFmGateway

): CompletableUseCaseWithParam<Pair<Long, Boolean>>(schedulers) {

    override fun buildUseCaseObservable(param: Pair<Long, Boolean>): Completable {
        val (artistId, isPodcast) = param
        return Completable.fromCallable {
            gateway.deleteTrack(artistId)

        }
    }
}