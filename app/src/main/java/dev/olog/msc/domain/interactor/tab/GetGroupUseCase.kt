package dev.olog.msc.domain.interactor.tab

import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.BaseGateway
import dev.olog.msc.domain.interactor.base.FlowableUseCase
import io.reactivex.Flowable

abstract class GetGroupUseCase<T>(
        private val gateway: BaseGateway<T, *>,
        schedulers: IoScheduler
) : FlowableUseCase<List<T>>(schedulers) {


    override fun buildUseCaseObservable(): Flowable<List<T>> = gateway.getAll()
}