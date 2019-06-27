package dev.olog.core.interactor.sort

import dev.olog.core.MediaId
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.executor.IoScheduler
import dev.olog.core.interactor.base.SingleUseCaseWithParam
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import kotlinx.coroutines.rx2.asFlowable
import javax.inject.Inject

class GetDetailSortDataUseCase @Inject constructor(
    scheduler: IoScheduler,
    private val getSortOrderUseCase: ObserveDetailSortOrderUseCase,
    private val getSortArrangingUseCase: GetSortArrangingUseCase

) : SingleUseCaseWithParam<SortEntity, MediaId>(scheduler){

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Single<SortEntity> {
        val arranging = getSortArrangingUseCase.execute().firstOrError()
        val sortOrder = getSortOrderUseCase(mediaId).asFlowable().firstOrError()
        return Singles.zip(arranging, sortOrder, { arr, sort ->  SortEntity(
                sort, arr
        )
        })
    }
}