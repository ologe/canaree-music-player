package dev.olog.msc.domain.interactor.last.fm

import dev.olog.core.MediaId
import dev.olog.msc.app.IoSchedulers
import dev.olog.msc.domain.gateway.LastFmGateway
import dev.olog.core.interactor.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class DeleteLastFmArtistUseCase @Inject constructor(
        schedulers: IoSchedulers,
        private val gateway: LastFmGateway

): CompletableUseCaseWithParam<MediaId>(schedulers) {

    override fun buildUseCaseObservable(param: MediaId): Completable {
        return Completable.fromCallable {
            gateway.deleteArtist(param.resolveId)
        }
    }
}