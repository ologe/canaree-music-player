package dev.olog.msc.domain.interactor.tab

import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.PlaylistGateway
import dev.olog.msc.domain.interactor.base.FlowableUseCase
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