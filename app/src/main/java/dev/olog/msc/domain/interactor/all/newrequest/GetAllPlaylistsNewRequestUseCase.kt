package dev.olog.msc.domain.interactor.all.newrequest

import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.domain.gateway.PlaylistGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class GetAllPlaylistsNewRequestUseCase @Inject constructor(
        schedulers: ComputationScheduler,
        private val gateway: PlaylistGateway

) : ObservableUseCase<List<Playlist>>(schedulers) {

    override fun buildUseCaseObservable(): Observable<List<Playlist>> {
        return gateway.getAllNewRequest()
    }
}