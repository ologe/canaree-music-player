package dev.olog.msc.domain.interactor.splash

import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import javax.inject.Inject

class FirstAccessUseCase @Inject constructor(
        private val prefs: AppPreferencesGateway

) {

    fun get() = prefs.isFirstAccess()

}