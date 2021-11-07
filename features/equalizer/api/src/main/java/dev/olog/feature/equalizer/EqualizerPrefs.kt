package dev.olog.feature.equalizer

import dev.olog.core.Preference
import dev.olog.core.Prefs

interface EqualizerPrefs : Prefs {

    val useCustomEqualizer: Preference<Boolean>

    val equalizerEnabled: Preference<Boolean>

    val bassBoostSettings: Preference<String>
    val virtualizerSettings: Preference<String>

    val currentPresetId: Preference<Long>

}