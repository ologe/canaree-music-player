package dev.olog.msc.domain.interactor.floating.window

import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.prefs.FloatingInfoPreferencesGateway
import dev.olog.msc.domain.interactor.base.FlowableUseCase
import io.reactivex.Flowable
import javax.inject.Inject

class GetFloatingInfoRequestUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val floatingInfoPreferencesGateway: FloatingInfoPreferencesGateway
) : FlowableUseCase<String>(scheduler) {

    override fun buildUseCaseObservable(): Flowable<String> {
        return floatingInfoPreferencesGateway.getInfoRequest()
    }
}