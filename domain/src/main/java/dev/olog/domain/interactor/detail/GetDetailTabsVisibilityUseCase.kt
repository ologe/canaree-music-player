package dev.olog.domain.interactor.detail

import dev.olog.domain.gateway.prefs.AppPreferencesGateway
import javax.inject.Inject

class GetDetailTabsVisibilityUseCase @Inject constructor(
        private val gateway: AppPreferencesGateway
) {

    fun execute(): BooleanArray {
        return gateway.getVisibleTabs()
    }

}