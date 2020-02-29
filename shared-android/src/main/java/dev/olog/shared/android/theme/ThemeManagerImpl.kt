package dev.olog.shared.android.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import dev.olog.shared.android.R
import dev.olog.shared.android.utils.isQ

class ThemeManagerImpl(
    private val context: Context,
    private val prefs: SharedPreferences
): ThemeManager {

    override val imageShape by lazy {
        val value = prefs.getString(
            context.getString(R.string.prefs_icon_shape_key),
            context.getString(R.string.prefs_icon_shape_rounded)
        )
        when (value) {
            context.getString(R.string.prefs_icon_shape_rounded) -> ImageShape.ROUND
            context.getString(R.string.prefs_icon_shape_square) -> ImageShape.RECTANGLE
            context.getString(R.string.prefs_icon_shape_cut_corner) -> ImageShape.CUT_CORNER
            else -> throw IllegalArgumentException("image shape not valid=$value")
        }
    }

    override val isDarkMode by lazy {
        val value = prefs.getString(
            context.getString(R.string.prefs_dark_mode_key),
            context.getString(R.string.prefs_dark_mode_2_entry_value_follow_system)
        )
        when (value) {
            context.getString(R.string.prefs_dark_mode_2_entry_value_follow_system) -> {
                if (isQ()) {
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                } else {
                    AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                }
            }
            context.getString(R.string.prefs_dark_mode_2_entry_value_light) -> AppCompatDelegate.MODE_NIGHT_NO
            context.getString(R.string.prefs_dark_mode_2_entry_value_dark) -> AppCompatDelegate.MODE_NIGHT_YES
            else -> throw IllegalStateException("invalid theme=$value")
        }
    }

    override val isImmersive by lazy {
        prefs.getBoolean(context.getString(R.string.prefs_immersive_key), false)

    }

    override val playerAppearance by lazy {
        val value = prefs.getString(
            context.getString(R.string.prefs_appearance_key),
            context.getString(R.string.prefs_appearance_entry_value_default)
        )
        when (value) {
            context.getString(R.string.prefs_appearance_entry_value_default) -> PlayerAppearance.DEFAULT
            context.getString(R.string.prefs_appearance_entry_value_flat) -> PlayerAppearance.FLAT
            context.getString(R.string.prefs_appearance_entry_value_spotify) -> PlayerAppearance.SPOTIFY
            context.getString(R.string.prefs_appearance_entry_value_fullscreen) -> PlayerAppearance.FULLSCREEN
            context.getString(R.string.prefs_appearance_entry_value_big_image) -> PlayerAppearance.BIG_IMAGE
            context.getString(R.string.prefs_appearance_entry_value_clean) -> PlayerAppearance.CLEAN
            context.getString(R.string.prefs_appearance_entry_value_mini) -> PlayerAppearance.MINI
            else -> throw IllegalStateException("invalid theme=$value")
        }
    }

    override val quickAction by lazy {
        val value = prefs.getString(
            context.getString(R.string.prefs_quick_action_key),
            context.getString(R.string.prefs_quick_action_entry_value_hide)
        )
        when (value) {
            context.getString(R.string.prefs_quick_action_entry_value_hide) -> QuickAction.NONE
            context.getString(R.string.prefs_quick_action_entry_value_play) -> QuickAction.PLAY
            else -> QuickAction.SHUFFLE
        }
    }

}