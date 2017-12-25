package dev.olog.domain.interactor.detail

import dev.olog.domain.gateway.prefs.AppPreferencesGateway
import javax.inject.Inject

class SetDetailTabsVisiblityUseCase @Inject constructor(
        private val gateway: AppPreferencesGateway
) {

    fun execute(list: List<Boolean>){
        gateway.setVisibleTabs(list)
    }


}