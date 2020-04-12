package dev.olog.feature.player.prefs

import android.content.Context
import android.content.SharedPreferences
import dev.olog.feature.player.R
import dev.olog.shared.android.extensions.observeKey
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class PlayerPreferences @Inject constructor(
    private val context: Context,
    private val preferences: SharedPreferences
) {

    fun observePlayerControlsVisibility(): Flow<Boolean> {
        return preferences.observeKey(context.getString(R.string.prefs_player_controls_visibility_key), false)
    }

    fun isAdaptiveColorEnabled(): Boolean {
        return preferences.getBoolean(context.getString(R.string.prefs_adaptive_colors_key), false)
    }

}