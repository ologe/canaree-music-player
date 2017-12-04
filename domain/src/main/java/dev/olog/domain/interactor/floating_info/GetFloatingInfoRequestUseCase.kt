package dev.olog.domain.interactor.floating_info

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.prefs.FloatingInfoPreferencesGateway
import dev.olog.domain.interactor.base.FlowableUseCase
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