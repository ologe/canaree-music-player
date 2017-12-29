package dev.olog.domain.interactor.detail.siblings

import dev.olog.domain.entity.Genre
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.GenreGateway
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.shared.MediaId
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject

class GetGenreSiblingsUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: GenreGateway

) : FlowableUseCaseWithParam<List<Genre>, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId) : Flowable<List<Genre>> {
        val genreId = mediaId.categoryValue.toLong()

        return gateway.getAll()
                .flatMapSingle { it.toFlowable()
                        .filter { it.id != genreId }
                        .toList()
                }
    }
}
