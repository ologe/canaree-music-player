package dev.olog.msc.domain.interactor.item

import dev.olog.msc.domain.entity.PodcastAlbum
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.PodcastAlbumGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCaseWithParam
import dev.olog.core.MediaId
import io.reactivex.Observable
import javax.inject.Inject

class GetPodcastAlbumUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: PodcastAlbumGateway

) : ObservableUseCaseWithParam<PodcastAlbum, MediaId>(schedulers) {

    override fun buildUseCaseObservable(param: MediaId): Observable<PodcastAlbum> {
        return gateway.getByParam(param.categoryId)
    }
}