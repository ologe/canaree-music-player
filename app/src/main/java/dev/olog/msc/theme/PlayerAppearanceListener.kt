package dev.olog.msc.theme

import android.content.Context
import android.content.SharedPreferences
import dev.olog.core.dagger.ApplicationContext
import dev.olog.shared.R
import dev.olog.shared.theme.PlayerAppearance
import javax.inject.Inject

internal class PlayerAppearanceListener @Inject constructor(
    @ApplicationContext context: Context,
    prefs: SharedPreferences
) : BaseThemeUpdater(context, prefs, context.getString(R.string.prefs_appearance_key)) {

    var playerAppearance = PlayerAppearance.DEFAULT

    override fun onPrefsChanged(forced: Boolean) {
        val value = prefs.getString(key, context.getString(R.string.prefs_appearance_entry_value_default))
        playerAppearance =  when (value) {
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
}
