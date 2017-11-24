package dev.olog.domain.interactor.detail.item

import dev.olog.domain.entity.Artist
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.ArtistGateway
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable
import javax.inject.Inject

class GetArtistUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: ArtistGateway

) : FlowableUseCaseWithParam<Artist, String>(schedulers) {


    override fun buildUseCaseObservable(mediaId: String): Flowable<Artist> {
        val categoryValue = MediaIdHelper.extractCategoryValue(mediaId)
        val artistId = categoryValue.toLong()

        return gateway.getByParam(artistId)
    }
}
