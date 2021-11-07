package dev.olog.feature.player

import dev.olog.core.Preference
import dev.olog.core.Prefs

interface PlayerPrefs : Prefs {

    val showPlayerControls: Preference<Boolean>

    // TODO wrong place
    val adaptiveColorEnabled: Preference<Boolean>

}