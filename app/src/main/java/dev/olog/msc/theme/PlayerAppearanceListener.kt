package dev.olog.msc.theme

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.msc.R
import dev.olog.msc.theme.observer.ActivityLifecycleCallbacks
import dev.olog.msc.theme.observer.CurrentActivityObserver
import dev.olog.shared.ConflatedSharedFlow
import dev.olog.shared.android.theme.PlayerAppearance
import dev.olog.shared.android.theme.PlayerAppearanceAmbient
import dev.olog.shared.value
import javax.inject.Inject

internal class PlayerAppearanceListener @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: SharedPreferences
) : BaseThemeUpdater(
    key = context.getString(R.string.prefs_appearance_key)
), ActivityLifecycleCallbacks by CurrentActivityObserver(context),
    PlayerAppearanceAmbient {

    private val _publisher = ConflatedSharedFlow(fetchValue())

    init {
        prefs.registerOnSharedPreferenceChangeListener(this)
    }

    override val value: PlayerAppearance
        get() = _publisher.value

    override fun onPrefsChanged() {
        _publisher.tryEmit(fetchValue())

        currentActivity?.recreate()
    }

    private fun fetchValue(): PlayerAppearance {
        val value = prefs.getString(key, context.getString(R.string.prefs_appearance_entry_value_default))

        return when (value) {
            context.getString(R.string.prefs_appearance_entry_value_default) -> PlayerAppearance.DEFAULT
            context.getString(R.string.prefs_appearance_entry_value_flat) -> PlayerAppearance.FLAT
            context.getString(R.string.prefs_appearance_entry_value_spotify) -> PlayerAppearance.SPOTIFY
            context.getString(R.string.prefs_appearance_entry_value_fullscreen) -> PlayerAppearance.FULLSCREEN
            context.getString(R.string.prefs_appearance_entry_value_big_image) -> PlayerAppearance.BIG_IMAGE
            context.getString(R.string.prefs_appearance_entry_value_clean) -> PlayerAppearance.CLEAN
            context.getString(R.string.prefs_appearance_entry_value_mini) -> PlayerAppearance.MINI
            else -> error("invalid theme=$value")
        }
    }

}
