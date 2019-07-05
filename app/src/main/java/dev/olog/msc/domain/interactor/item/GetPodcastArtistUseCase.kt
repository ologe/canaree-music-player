package dev.olog.msc.domain.interactor.item

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Artist
import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.interactor.base.ObservableUseCaseWithParam
import io.reactivex.Observable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

class GetPodcastArtistUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: PodcastArtistGateway

) : ObservableUseCaseWithParam<Artist, MediaId>(schedulers) {

    override fun buildUseCaseObservable(param: MediaId): Observable<Artist> {
        return gateway.observeByParam(param.categoryId).map { it!! }.asObservable()
    }
}