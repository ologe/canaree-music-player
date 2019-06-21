package dev.olog.msc.domain.interactor.item

import dev.olog.core.entity.PodcastPlaylist
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.PodcastPlaylistGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCaseWithParam
import dev.olog.core.MediaId
import io.reactivex.Observable
import javax.inject.Inject

class GetPodcastPlaylistUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: PodcastPlaylistGateway

) : ObservableUseCaseWithParam<PodcastPlaylist, MediaId>(schedulers) {

    override fun buildUseCaseObservable(param: MediaId): Observable<PodcastPlaylist> {
        return gateway.getByParam(param.categoryId)
    }
}