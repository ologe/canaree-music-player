package dev.olog.msc.domain.interactor.lastplayed

import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.AlbumGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class InsertLastPlayedAlbumUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val albumGateway: AlbumGateway

): CompletableUseCaseWithParam<Long>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(albumId: Long): Completable {
        return albumGateway.addLastPlayed(albumId)
    }
}