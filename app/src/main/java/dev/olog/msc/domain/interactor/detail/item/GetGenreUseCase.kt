package dev.olog.msc.domain.interactor.detail.item

import dev.olog.msc.domain.entity.Genre
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.GenreGateway
import dev.olog.msc.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.msc.utils.MediaId
import io.reactivex.Flowable
import javax.inject.Inject

class GetGenreUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: GenreGateway

) : FlowableUseCaseWithParam<Genre, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Flowable<Genre> {
        val genreId = mediaId.categoryValue.toLong()

        return gateway.getByParam(genreId)
    }
}
