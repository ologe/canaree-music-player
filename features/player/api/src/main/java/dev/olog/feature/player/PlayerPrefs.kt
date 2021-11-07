package dev.olog.feature.player

import dev.olog.core.Preference
import dev.olog.core.Prefs
import dev.olog.shared.android.theme.PlayerAppearance

interface PlayerPrefs : Prefs {

    val showPlayerControls: Preference<Boolean>

    val appearance: Preference<PlayerAppearance>

}