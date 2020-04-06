package dev.olog.domain.interactor

import dev.olog.domain.prefs.AppPreferencesGateway
import dev.olog.domain.prefs.BlacklistPreferences
import dev.olog.domain.prefs.EqualizerPreferencesGateway
import dev.olog.domain.prefs.MusicPreferencesGateway
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