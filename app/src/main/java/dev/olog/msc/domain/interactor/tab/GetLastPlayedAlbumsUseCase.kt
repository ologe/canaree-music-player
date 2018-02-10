package dev.olog.msc.domain.interactor.tab

import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.AlbumGateway
import dev.olog.msc.domain.interactor.base.FlowableUseCase
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