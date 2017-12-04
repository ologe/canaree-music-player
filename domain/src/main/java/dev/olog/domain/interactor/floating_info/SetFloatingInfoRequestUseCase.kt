package dev.olog.domain.interactor.floating_info

import dev.olog.domain.gateway.prefs.FloatingInfoPreferencesGateway
import javax.inject.Inject

class SetFloatingInfoRequestUseCase @Inject constructor(
        private val floatingInfoPreferencesGateway: FloatingInfoPreferencesGateway
){

    fun execute(newInfoRequest: String){
        floatingInfoPreferencesGateway.setInfoRequest(newInfoRequest)
    }

}