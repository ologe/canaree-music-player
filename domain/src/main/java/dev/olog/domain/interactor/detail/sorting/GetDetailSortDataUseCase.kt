package dev.olog.domain.interactor.detail.sorting

import dev.olog.domain.entity.DetailSort
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.interactor.base.SingleUseCaseWithParam
import dev.olog.shared.MediaId
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import javax.inject.Inject

class GetDetailSortDataUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val getSortOrderUseCase: GetSortOrderUseCase,
        private val getSortArrangingUseCase: GetSortArrangingUseCase

) : SingleUseCaseWithParam<DetailSort, MediaId>(scheduler){

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Single<DetailSort> {
        val arranging = getSortArrangingUseCase.execute().firstOrError()
        val sortOrder = getSortOrderUseCase.execute(mediaId).firstOrError()
        return Singles.zip(arranging, sortOrder, { arr, sort ->  DetailSort(
                sort, arr
        )})
    }
}