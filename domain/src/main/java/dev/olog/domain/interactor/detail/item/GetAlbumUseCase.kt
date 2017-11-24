package dev.olog.domain.interactor.detail.item

import dev.olog.domain.entity.Album
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.AlbumGateway
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable
import javax.inject.Inject

class GetAlbumUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: AlbumGateway

) : FlowableUseCaseWithParam<Album, String>(schedulers) {


    override fun buildUseCaseObservable(mediaId: String): Flowable<Album> {
        val categoryValue = MediaIdHelper.extractCategoryValue(mediaId)
        val artistId = categoryValue.toLong()

        return gateway.getByParam(artistId)
    }
}
