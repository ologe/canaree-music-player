package dev.olog.domain.gateway.prefs

interface EqualizerPreferencesGateway {

    fun isEqualizerEnabled(): Boolean
    fun setEqualizerEnabled(enabled: Boolean)

    fun isReplayGainEnabled(): Boolean
    fun setReplayGainEnabled(enabled: Boolean)

    fun saveEqualizerSettings(settings: String)
    fun saveBassBoostSettings(settings: String)
    fun saveVirtualizerSettings(settings: String)

}