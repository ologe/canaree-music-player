package dev.olog.feature.player

import dev.olog.core.Preference
import dev.olog.core.PreferenceManager
import javax.inject.Inject

class PlayerPrefsImpl @Inject constructor(
    private val prefs: PreferenceManager,
) : PlayerPrefs {

    override val showPlayerControls: Preference<Boolean>
        get() = prefs.create(dev.olog.prefskeys.R.string.prefs_player_controls_visibility_key, false)

    override val adaptiveColorEnabled: Preference<Boolean>
        get() =  prefs.create(dev.olog.prefskeys.R.string.prefs_adaptive_colors_key, false)

}