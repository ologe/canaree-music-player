package dev.olog.msc.domain.interactor.item

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Playlist
import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.PodcastPlaylistGateway2
import dev.olog.msc.domain.interactor.base.ObservableUseCaseWithParam
import io.reactivex.Observable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

class GetPodcastPlaylistUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: PodcastPlaylistGateway2

) : ObservableUseCaseWithParam<Playlist, MediaId>(schedulers) {

    override fun buildUseCaseObservable(param: MediaId): Observable<Playlist> {
        return gateway.observeByParam(param.categoryId).map { it!! }.asObservable()
    }
}