package dev.olog.domain.interactor.detail.siblings

import dev.olog.domain.entity.Genre
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.GenreGateway
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject

class GetGenreSiblingsUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: GenreGateway

) : FlowableUseCaseWithParam<List<Genre>, String>(schedulers) {


    override fun buildUseCaseObservable(mediaId: String) : Flowable<List<Genre>> {
        val categoryValue = MediaIdHelper.extractCategoryValue(mediaId)
        val genreId = categoryValue.toLong()

        return gateway.getAll()
                .flatMapSingle {
                    it.toFlowable().filter { it.id != genreId }.toList()
                }
                .map { if (it.size > 1) it else listOf() }
    }
}
