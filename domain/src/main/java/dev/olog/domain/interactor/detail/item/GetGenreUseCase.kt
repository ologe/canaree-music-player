package dev.olog.domain.interactor.detail.item

import dev.olog.domain.entity.Genre
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.GenreGateway
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable
import javax.inject.Inject

class GetGenreUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: GenreGateway

) : FlowableUseCaseWithParam<Genre, String>(schedulers) {


    override fun buildUseCaseObservable(mediaId: String): Flowable<Genre> {
        val categoryValue = MediaIdHelper.extractCategoryValue(mediaId)
        val artistId = categoryValue.toLong()

        return gateway.getByParam(artistId)
    }
}
