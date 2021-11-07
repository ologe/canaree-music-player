package dev.olog.feature.equalizer

import dev.olog.core.Preference
import dev.olog.core.PreferenceManager
import javax.inject.Inject

class EqualizerPreferenceImpl @Inject constructor(
    private val preferenceManager: PreferenceManager,
) : EqualizerPrefs {

    companion object {
        private const val TAG = "EqualizerPreferenceImpl"

        private const val EQ_ENABLED = "$TAG.EQ_ENABLED"

        private const val EQ_PRESET_ID = "$TAG.EQ_PRESET_ID"
        private const val BASS_BOOST_SETTINGS = "$TAG.BASS_BOOST_SETTINGS"
        private const val VIRTUALIZER_SETTINGS = "$TAG.VIRTUALIZER_SETTINGS"
    }

    override val useCustomEqualizer: Preference<Boolean>
        get() = preferenceManager.create(dev.olog.prefskeys.R.string.prefs_used_equalizer_key, true)

    override val equalizerEnabled: Preference<Boolean>
        get() = preferenceManager.create(EQ_ENABLED, false)

    override val virtualizerSettings: Preference<String>
        get() = preferenceManager.create(VIRTUALIZER_SETTINGS, "")

    override val bassBoostSettings: Preference<String>
        get() = preferenceManager.create(BASS_BOOST_SETTINGS, "")

    override val currentPresetId: Preference<Long>
        get() = preferenceManager.create(EQ_PRESET_ID, 0L)

}