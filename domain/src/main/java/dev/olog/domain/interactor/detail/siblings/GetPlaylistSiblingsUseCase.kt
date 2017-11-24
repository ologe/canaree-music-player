package dev.olog.domain.interactor.detail.siblings

import dev.olog.domain.entity.Playlist
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.PlaylistGateway
import dev.olog.domain.interactor.base.FlowableUseCase
import io.reactivex.Flowable
import javax.inject.Inject

class GetPlaylistSiblingsUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: PlaylistGateway

) : FlowableUseCase<List<Playlist>>(schedulers) {

    override fun buildUseCaseObservable(): Flowable<List<Playlist>> = gateway.getAll().filter { it.size > 1 }
}
