package dev.olog.domain.interactor.detail.item

import dev.olog.domain.entity.Album
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.AlbumGateway
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.shared.MediaId
import io.reactivex.Flowable
import javax.inject.Inject

class GetAlbumUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: AlbumGateway

) : FlowableUseCaseWithParam<Album, MediaId>(schedulers) {


    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Flowable<Album> {
        val albumId = mediaId.categoryValue.toLong()
        return gateway.getByParam(albumId)
    }
}
