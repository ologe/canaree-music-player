package dev.olog.core.interactor

import dev.olog.core.gateway.BlacklistGateway
import dev.olog.core.prefs.AppPreferencesGateway
import dev.olog.core.prefs.EqualizerPreferencesGateway
import dev.olog.core.prefs.MusicPreferencesGateway
import javax.inject.Inject

class ResetPreferencesUseCase @Inject constructor(
    private val appPrefsUseCase: AppPreferencesGateway,
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    private val equalizerPrefsUseCase: EqualizerPreferencesGateway,
    private val blacklistGateway: BlacklistGateway
) {

    suspend fun execute() {
        appPrefsUseCase.setDefault()
        musicPreferencesUseCase.setDefault()
        equalizerPrefsUseCase.setDefault()
        blacklistGateway.reset()
    }

}