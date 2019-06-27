package dev.olog.msc.domain.interactor.item

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Song
import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.PodcastGateway
import dev.olog.core.interactor.base.ObservableUseCaseWithParam
import io.reactivex.Observable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

class GetPodcastUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: PodcastGateway

) : ObservableUseCaseWithParam<Song, MediaId>(schedulers) {

    override fun buildUseCaseObservable(param: MediaId): Observable<Song> {
        return gateway.observeByParam(param.resolveId).map { it!! }.asObservable()
    }
}
