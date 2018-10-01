package dev.olog.msc.domain.interactor.all

import dev.olog.msc.domain.entity.PodcastPlaylist
import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.domain.gateway.PodcastPlaylistGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class GetAllPodcastsAutoPlaylistUseCase @Inject constructor(
        schedulers: ComputationScheduler,
        private val gateway: PodcastPlaylistGateway

) : ObservableUseCase<List<PodcastPlaylist>>(schedulers) {

    override fun buildUseCaseObservable(): Observable<List<PodcastPlaylist>> {
        return gateway.getAllAutoPlaylists()
    }
}