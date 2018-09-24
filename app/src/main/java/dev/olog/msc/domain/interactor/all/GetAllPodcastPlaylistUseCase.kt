package dev.olog.msc.domain.interactor.all

import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.domain.gateway.PlaylistGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class GetAllPodcastPlaylistUseCase @Inject constructor(
        private val gateway: PlaylistGateway,
        schedulers: ComputationScheduler
) : ObservableUseCase<List<Playlist>>(schedulers) {

    override fun buildUseCaseObservable(): Observable<List<Playlist>> {
        return gateway.getAllPodcastPlaylsts()
    }
}