package dev.olog.msc.domain.interactor.all

import dev.olog.core.entity.track.Playlist
import dev.olog.core.executor.ComputationScheduler
import dev.olog.msc.domain.gateway.PlaylistGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class GetAllAutoPlaylistUseCase @Inject constructor(
    schedulers: ComputationScheduler,
    private val gateway: PlaylistGateway

) : ObservableUseCase<List<Playlist>>(schedulers) {

    override fun buildUseCaseObservable(): Observable<List<Playlist>> {
        return gateway.getAllAutoPlaylists()
    }
}