package dev.olog.msc.domain.interactor.last.fm

import dev.olog.core.gateway.LastFmGateway
import dev.olog.core.interactor.base.CompletableUseCaseWithParam
import dev.olog.injection.IoSchedulers
import io.reactivex.Completable
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class DeleteLastFmTrackUseCase @Inject constructor(
    schedulers: IoSchedulers,
    private val gateway: LastFmGateway

): CompletableUseCaseWithParam<Long>(schedulers) {

    override fun buildUseCaseObservable(artistId: Long): Completable {
        return Completable.fromCallable {
            runBlocking { gateway.deleteTrack(artistId) }

        }
    }
}