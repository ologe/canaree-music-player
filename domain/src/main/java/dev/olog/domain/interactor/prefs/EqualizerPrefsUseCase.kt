package dev.olog.domain.interactor.prefs

import dev.olog.domain.gateway.prefs.EqualizerPreferencesGateway
import javax.inject.Inject

class EqualizerPrefsUseCase @Inject constructor(
        private val gateway: EqualizerPreferencesGateway
) {


    fun isEqualizerEnabled() = gateway.isEqualizerEnabled()

    fun setEqualizerEnabled(enabled: Boolean){
        gateway.setEqualizerEnabled(enabled)
    }

    fun isReplayGainEnabled() = gateway.isReplayGainEnabled()

    fun setReplayGainEnabled(enabled: Boolean) {
        gateway.setReplayGainEnabled(enabled)
    }

    fun saveEqualizerSettings(settings: String){
        gateway.saveEqualizerSettings(settings)
    }

    fun saveBassBoostSettings(settings: String){
        gateway.saveBassBoostSettings(settings)
    }

    fun saveVirtualizerSettings(settings: String){
        gateway.saveVirtualizerSettings(settings)
    }

}