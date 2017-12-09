package dev.olog.domain.interactor.tab

import dev.olog.domain.entity.Playlist
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.PlaylistGateway
import dev.olog.domain.interactor.base.FlowableUseCase
import io.reactivex.Flowable
import javax.inject.Inject

class GetAllAutoPlaylistUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: PlaylistGateway

) : FlowableUseCase<List<Playlist>>(schedulers) {

    override fun buildUseCaseObservable(): Flowable<List<Playlist>> {
        return gateway.getAllAutoPlaylists()
    }
}