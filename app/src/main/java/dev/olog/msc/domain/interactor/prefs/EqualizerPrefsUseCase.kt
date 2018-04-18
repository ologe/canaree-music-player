package dev.olog.msc.domain.interactor.prefs

import dev.olog.msc.domain.gateway.prefs.EqualizerPreferencesGateway
import io.reactivex.Completable
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

    fun getEqualizerSettings(): String {
        return gateway.getEqualizerSettings()
    }

    fun getBassBoostSettings(): String {
        return gateway.getBassBoostSettings()
    }

    fun getVirtualizerSettings(): String {
        return gateway.getVirtualizerSettings()
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

    fun setDefault(): Completable{
        return gateway.setDefault()
    }

}