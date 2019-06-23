package dev.olog.msc.domain.interactor.all.sorted.util

import dev.olog.core.MediaId
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.executor.IoScheduler
import dev.olog.msc.domain.interactor.base.SingleUseCaseWithParam
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import javax.inject.Inject

class GetDetailSortDataUseCase @Inject constructor(
    scheduler: IoScheduler,
    private val getSortOrderUseCase: GetSortOrderUseCase,
    private val getSortArrangingUseCase: GetSortArrangingUseCase

) : SingleUseCaseWithParam<SortEntity, MediaId>(scheduler){

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Single<SortEntity> {
        val arranging = getSortArrangingUseCase.execute().firstOrError()
        val sortOrder = getSortOrderUseCase.execute(mediaId).firstOrError()
        return Singles.zip(arranging, sortOrder, { arr, sort ->  SortEntity(
                sort, arr
        )
        })
    }
}