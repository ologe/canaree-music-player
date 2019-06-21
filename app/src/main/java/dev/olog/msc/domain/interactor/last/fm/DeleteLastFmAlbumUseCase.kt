package dev.olog.msc.domain.interactor.last.fm

import dev.olog.core.MediaId
import dev.olog.msc.app.IoSchedulers
import dev.olog.msc.domain.gateway.LastFmGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class DeleteLastFmAlbumUseCase @Inject constructor(
        schedulers: IoSchedulers,
        private val gateway: LastFmGateway

): CompletableUseCaseWithParam<MediaId>(schedulers) {

    override fun buildUseCaseObservable(param: MediaId): Completable {
        return Completable.fromCallable {
            gateway.deleteAlbum(param.resolveId)

        }
    }
}