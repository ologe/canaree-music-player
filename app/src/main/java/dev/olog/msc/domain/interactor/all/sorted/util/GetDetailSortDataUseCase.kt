package dev.olog.msc.domain.interactor.all.sorted.util

import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.interactor.base.SingleUseCaseWithParam
import dev.olog.msc.presentation.detail.sort.DetailSort
import dev.olog.core.MediaId
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
        )
        })
    }
}