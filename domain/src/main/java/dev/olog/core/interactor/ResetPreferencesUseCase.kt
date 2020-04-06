package dev.olog.core.interactor

import dev.olog.core.prefs.AppPreferencesGateway
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.EqualizerPreferencesGateway
import dev.olog.core.prefs.MusicPreferencesGateway
import javax.inject.Inject

class ResetPreferencesUseCase @Inject constructor(
    private val appPrefsUseCase: AppPreferencesGateway,
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    private val equalizerPrefsUseCase: EqualizerPreferencesGateway,
    private val blacklistPreferences: BlacklistPreferences
) {

    operator fun invoke() {
        appPrefsUseCase.setDefault()
        musicPreferencesUseCase.setDefault()
        equalizerPrefsUseCase.setDefault()
        blacklistPreferences.setDefault()
    }

}