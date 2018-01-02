package dev.olog.domain.interactor

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.domain.interactor.base.FlowableUseCase
import io.reactivex.Flowable
import javax.inject.Inject

class GetIsIconDarkUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val gateway: AppPreferencesGateway

) : FlowableUseCase<Boolean>(scheduler) {

    override fun buildUseCaseObservable(): Flowable<Boolean> {
        return gateway.isIconsDark()
    }
}