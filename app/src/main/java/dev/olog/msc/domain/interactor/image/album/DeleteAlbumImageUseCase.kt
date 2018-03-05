package dev.olog.msc.domain.interactor.image.album

import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.LastFmGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class DeleteAlbumImageUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: LastFmGateway

) : CompletableUseCaseWithParam<Album>(schedulers) {

    override fun buildUseCaseObservable(param: Album): Completable {
        return gateway.deleteAlbumImage(param.id)
    }
}