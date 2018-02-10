package dev.olog.msc.domain.interactor

import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.AlbumGateway
import dev.olog.msc.domain.interactor.base.FlowableUseCase
import io.reactivex.Flowable
import javax.inject.Inject

class GetAllAlbumsForUtilsUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val albumGateway: AlbumGateway
): FlowableUseCase<List<Album>>(scheduler) {

    override fun buildUseCaseObservable(): Flowable<List<Album>> {
        return albumGateway.getAllAlbumsForUtils()
    }
}