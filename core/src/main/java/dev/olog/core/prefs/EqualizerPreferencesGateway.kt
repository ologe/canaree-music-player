package dev.olog.core.prefs

import dev.olog.core.Resettable
import kotlinx.coroutines.flow.Flow

interface EqualizerPreferencesGateway : Resettable {

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