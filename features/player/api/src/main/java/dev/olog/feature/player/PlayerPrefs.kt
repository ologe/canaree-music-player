package dev.olog.feature.player

import dev.olog.core.Preference

interface PlayerPrefs {

    val showPlayerControls: Preference<Boolean>

    // TODO wrong place
    val adaptiveColorEnabled: Preference<Boolean>

}