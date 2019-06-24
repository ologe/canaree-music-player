package dev.olog.msc.domain.interactor.last.fm

import dev.olog.core.MediaId
import dev.olog.core.gateway.LastFmGateway2
import dev.olog.core.interactor.CompletableUseCaseWithParam
import dev.olog.injection.IoSchedulers
import io.reactivex.Completable
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class DeleteLastFmAlbumUseCase @Inject constructor(
    schedulers: IoSchedulers,
    private val gateway: LastFmGateway2

): CompletableUseCaseWithParam<MediaId>(schedulers) {

    override fun buildUseCaseObservable(param: MediaId): Completable {
        return Completable.fromCallable {
            runBlocking { gateway.deleteAlbum(param.resolveId) }

        }
    }
}