package dev.olog.feature.player

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.Preference
import dev.olog.core.PreferenceManager
import dev.olog.shared.android.theme.PlayerAppearance
import javax.inject.Inject

class PlayerPrefsImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferenceManager: PreferenceManager,
) : PlayerPrefs {

    override val showPlayerControls: Preference<Boolean>
        get() = preferenceManager.create(prefs.R.string.prefs_player_controls_visibility_key, false)

    override val appearance: Preference<PlayerAppearance>
        get() = preferenceManager.createWithMapper(
            key = context.getString(prefs.R.string.prefs_appearance_key),
            default = PlayerAppearance.DEFAULT,
            serialize = { context.getString(it.prefValue) },
            deserialize = { PlayerAppearance.fromPref(context, it) }
        )
}