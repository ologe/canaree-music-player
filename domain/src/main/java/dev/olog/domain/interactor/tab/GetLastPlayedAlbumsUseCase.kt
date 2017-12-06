package dev.olog.domain.interactor.tab

import dev.olog.domain.entity.Album
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.AlbumGateway
import dev.olog.domain.interactor.base.FlowableUseCase
import io.reactivex.Flowable
import javax.inject.Inject

class GetLastPlayedAlbumsUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val albumGateway: AlbumGateway

): FlowableUseCase<List<Album>>(schedulers) {

    override fun buildUseCaseObservable(): Flowable<List<Album>> {
        return albumGateway.getLastPlayed()
    }
}