package dev.olog.domain.interactor.detail

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.domain.interactor.base.FlowableUseCase
import javax.inject.Inject

class ObserveDetailTabsVisiblityUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val gateway: AppPreferencesGateway

) : FlowableUseCase<List<Boolean>>(scheduler) {

    override fun buildUseCaseObservable() = gateway.observeVisibleTabs()
}