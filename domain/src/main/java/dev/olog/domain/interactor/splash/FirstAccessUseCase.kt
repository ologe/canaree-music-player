package dev.olog.domain.interactor.splash

import dev.olog.domain.gateway.prefs.AppPreferencesGateway
import javax.inject.Inject

class FirstAccessUseCase @Inject constructor(
        private val prefs: AppPreferencesGateway
) {

    fun get() = prefs.isFirstAccess()

}