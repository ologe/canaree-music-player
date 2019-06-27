package dev.olog.core.prefs

interface EqualizerPreferencesGateway {

    fun isEqualizerEnabled(): Boolean
    fun setEqualizerEnabled(enabled: Boolean)

    fun saveEqualizerSettings(settings: String)
    fun saveBassBoostSettings(settings: String)
    fun saveVirtualizerSettings(settings: String)

    fun getEqualizerSettings(): String
    fun getVirtualizerSettings(): String
    fun getBassBoostSettings(): String
    fun setDefault()

}