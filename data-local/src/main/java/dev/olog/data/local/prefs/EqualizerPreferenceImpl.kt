package dev.olog.data.local.prefs

import android.content.SharedPreferences
import androidx.core.content.edit
import dev.olog.core.prefs.EqualizerPreferencesGateway
import dev.olog.shared.android.extensions.observeKey
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EqualizerPreferenceImpl @Inject constructor(
    private val preferences: SharedPreferences

) : EqualizerPreferencesGateway {

    companion object {
        private const val TAG = "EqualizerPreferenceImpl"

        private const val EQ_ENABLED = "$TAG.EQ_ENABLED"

        private const val EQ_PRESET_ID = "$TAG.EQ_PRESET_ID"
        private const val BASS_BOOST_SETTINGS = "$TAG.BASS_BOOST_SETTINGS"
        private const val VIRTUALIZER_SETTINGS = "$TAG.VIRTUALIZER_SETTINGS"
    }

    override fun isEqualizerEnabled(): Boolean {
        return preferences.getBoolean(EQ_ENABLED, false)
    }

    override fun setEqualizerEnabled(enabled: Boolean) {
        return preferences.edit { putBoolean(EQ_ENABLED, enabled) }
    }

    override fun getVirtualizerSettings(): String {
        return preferences.getString(VIRTUALIZER_SETTINGS, "")!!
    }

    override fun getBassBoostSettings(): String {
        return preferences.getString(BASS_BOOST_SETTINGS, "")!!
    }

    override fun getCurrentPresetId(): Long {
        return preferences.getLong(EQ_PRESET_ID, 0L)
    }

    override fun observeCurrentPresetId(): Flow<Long> {
        return preferences.observeKey(EQ_PRESET_ID, 0L)
    }

    override fun setCurrentPresetId(id: Long) {
        preferences.edit {
            putLong(EQ_PRESET_ID, id)
        }
    }

    override fun saveBassBoostSettings(settings: String) {
        preferences.edit { putString(BASS_BOOST_SETTINGS, settings) }
    }

    override fun saveVirtualizerSettings(settings: String) {
        preferences.edit { putString(VIRTUALIZER_SETTINGS, settings) }
    }

    override fun setDefault() {
        setEqualizerEnabled(false)
    }
}