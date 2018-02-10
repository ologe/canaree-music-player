package dev.olog.msc.domain.interactor.detail.siblings

import dev.olog.msc.domain.entity.Genre
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.GenreGateway
import dev.olog.msc.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.msc.utils.MediaId
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
