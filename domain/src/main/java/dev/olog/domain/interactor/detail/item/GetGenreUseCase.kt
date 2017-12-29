package dev.olog.domain.interactor.detail.item

import dev.olog.domain.entity.Genre
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.GenreGateway
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.shared.MediaId
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
