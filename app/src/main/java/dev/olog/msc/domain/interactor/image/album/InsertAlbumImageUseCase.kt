package dev.olog.msc.domain.interactor.image.album

import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.LastFmGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class InsertAlbumImageUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: LastFmGateway

) : CompletableUseCaseWithParam<Pair<Album, String>>(schedulers) {

    override fun buildUseCaseObservable(param: Pair<Album, String>): Completable {
        val (album, image) = param

        return gateway.insertAlbumImage(album.id, image)
    }
}