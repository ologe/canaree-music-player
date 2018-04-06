package dev.olog.msc.data.prefs

import android.content.SharedPreferences
import androidx.core.content.edit
import dev.olog.msc.domain.gateway.prefs.EqualizerPreferencesGateway
import javax.inject.Inject

class EqualizerPreferenceImpl @Inject constructor(
        private val preferences: SharedPreferences

): EqualizerPreferencesGateway {

    companion object {
        private const val TAG = "EqualizerPreferenceImpl"

        private const val EQ_ENABLED = "$TAG.EQ_ENABLED"
        private const val REPLAY_GAIN_ENABLED = "$TAG.REPLAY_GAIN_ENABLED"

        private const val EQ_SETTINGS = "$TAG.EQ_SETTINGS"
        private const val BASS_BOOST_SETTINGS = "$TAG.BASS_BOOST_SETTINGS"
        private const val VIRTUALIZER_SETTINGS = "$TAG.VIRTUALIZER_SETTINGS"
    }

    override fun isEqualizerEnabled(): Boolean {
        return preferences.getBoolean(EQ_ENABLED, false)
    }

    override fun setEqualizerEnabled(enabled: Boolean) {
        return preferences.edit { putBoolean(EQ_ENABLED, enabled) }
    }

    override fun isReplayGainEnabled(): Boolean {
        return preferences.getBoolean(REPLAY_GAIN_ENABLED, false)
    }

    override fun setReplayGainEnabled(enabled: Boolean) {
        return preferences.edit { putBoolean(REPLAY_GAIN_ENABLED, enabled) }
    }

    override fun getEqualizerSettings(): String {
        return preferences.getString(EQ_SETTINGS, "")
    }

    override fun getVirtualizerSettings(): String {
        return preferences.getString(VIRTUALIZER_SETTINGS, "")
    }

    override fun getBassBoostSettings(): String {
        return preferences.getString(BASS_BOOST_SETTINGS, "")
    }

    override fun saveEqualizerSettings(settings: String) {
        preferences.edit { putString(EQ_SETTINGS, settings) }
    }

    override fun saveBassBoostSettings(settings: String) {
        preferences.edit { putString(BASS_BOOST_SETTINGS, settings) }
    }

    override fun saveVirtualizerSettings(settings: String) {
        preferences.edit { putString(VIRTUALIZER_SETTINGS, settings) }
    }


}