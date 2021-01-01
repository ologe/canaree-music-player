package dev.olog.domain.prefs

import dev.olog.domain.ResettablePreference
import kotlinx.coroutines.flow.Flow

interface EqualizerPreferencesGateway : ResettablePreference {

    fun isEqualizerEnabled(): Boolean
    fun setEqualizerEnabled(enabled: Boolean)

    fun saveBassBoostSettings(settings: String)
    fun saveVirtualizerSettings(settings: String)

    fun getCurrentPresetId(): Long
    fun observeCurrentPresetId(): Flow<Long>
    fun setCurrentPresetId(id: Long)

    fun getVirtualizerSettings(): String
    fun getBassBoostSettings(): String

}