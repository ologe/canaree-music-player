package dev.olog.shared.android.theme

import android.content.Context
import android.content.SharedPreferences
import dev.olog.shared.ApplicationContext
import dev.olog.shared.android.DarkModeUtils
import dev.olog.shared.android.R
import javax.inject.Inject

internal class ThemeManagerImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val prefs: SharedPreferences
) : ThemeManager {

    override val imageShape: ImageShape
        get() {
            val value = prefs.getString(
                context.getString(R.string.prefs_icon_shape_key),
                context.getString(R.string.prefs_icon_shape_rounded)
            )!!
            return when (value) {
                context.getString(R.string.prefs_icon_shape_rounded) -> ImageShape.ROUND
                context.getString(R.string.prefs_icon_shape_square) -> ImageShape.RECTANGLE
                context.getString(R.string.prefs_icon_shape_cut_corner) -> ImageShape.CUT_CORNER
                else -> throw IllegalArgumentException("image shape not valid=$value")
            }
        }

    override val isImmersive: Boolean
        get() = prefs.getBoolean(context.getString(R.string.prefs_immersive_key), false)

    override val playerAppearance: PlayerAppearance
        get() {
            val value = prefs.getString(
                context.getString(R.string.prefs_appearance_key),
                context.getString(R.string.prefs_appearance_entry_value_default)
            )!!
            return when (value) {
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

    override val quickAction: QuickAction
        get() {
            val value = prefs.getString(
                context.getString(R.string.prefs_quick_action_key),
                context.getString(R.string.prefs_quick_action_entry_value_hide)
            )!!
            return when (value) {
                context.getString(R.string.prefs_quick_action_entry_value_hide) -> QuickAction.NONE
                context.getString(R.string.prefs_quick_action_entry_value_play) -> QuickAction.PLAY
                else -> QuickAction.SHUFFLE
            }
        }

    override val bottomSheetType: BottomSheetType
        get() {
            val value = prefs.getString(
                context.getString(R.string.prefs_mini_player_appearance_key),
                context.getString(R.string.prefs_mini_player_appearance_entry_value_default)
            )!!
            return when (value) {
                context.getString(R.string.prefs_mini_player_appearance_entry_value_default) -> BottomSheetType.DEFAULT
                context.getString(R.string.prefs_mini_player_appearance_entry_value_floating) -> BottomSheetType.FLOATING
                else -> throw IllegalStateException("invalid preference value $value for key:${context.getString(R.string.prefs_mini_player_appearance_key)}")
            }
        }


    override val darkMode: Int
        get() {
            val value = prefs.getString(
                context.getString(R.string.prefs_dark_mode_key),
                context.getString(R.string.prefs_dark_mode_2_entry_value_follow_system)
            )!!
            return DarkModeUtils.fromString(context, value)
        }
}