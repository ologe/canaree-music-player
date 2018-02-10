package dev.olog.msc.domain.interactor.prefs

import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.domain.interactor.base.FlowableUseCase
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