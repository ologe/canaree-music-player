package dev.olog.msc.domain.interactor.floating.window

import dev.olog.msc.domain.gateway.prefs.FloatingInfoPreferencesGateway
import javax.inject.Inject

class SetFloatingInfoRequestUseCase @Inject constructor(
        private val floatingInfoPreferencesGateway: FloatingInfoPreferencesGateway
){

    fun execute(newInfoRequest: String){
        floatingInfoPreferencesGateway.setInfoRequest(newInfoRequest)
    }

}