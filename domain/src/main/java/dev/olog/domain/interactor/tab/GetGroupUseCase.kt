package dev.olog.domain.interactor.tab

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.BaseGateway
import dev.olog.domain.interactor.base.FlowableUseCase
import io.reactivex.Flowable

abstract class GetGroupUseCase<T>(
        private val gateway: BaseGateway<T, *>,
        schedulers: IoScheduler
) : FlowableUseCase<List<T>>(schedulers) {


    override fun buildUseCaseObservable(): Flowable<List<T>> = gateway.getAll()
}