package dev.olog.domain.interactor

import dev.olog.domain.entity.SmallPlayType
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.domain.interactor.base.FlowableUseCase
import io.reactivex.Flowable
import javax.inject.Inject

class GetSmallPlayType @Inject constructor(
        scheduler: IoScheduler,
        private val gateway: AppPreferencesGateway

) : FlowableUseCase<SmallPlayType>(scheduler) {

    override fun buildUseCaseObservable(): Flowable<SmallPlayType> {
        return gateway.getSmallPlay()
    }
}