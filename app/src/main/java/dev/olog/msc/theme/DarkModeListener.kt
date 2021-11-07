package dev.olog.msc.theme

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.feature.main.MainPrefs
import dev.olog.msc.theme.observer.ActivityLifecycleCallbacks
import dev.olog.msc.theme.observer.CurrentActivityObserver
import dev.olog.shared.android.theme.DarkMode
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

internal class DarkModeListener @Inject constructor(
    @ApplicationContext private val context: Context,
    appScope: CoroutineScope,
    mainPrefs: MainPrefs,
) : BaseThemeUpdater<DarkMode>(appScope, mainPrefs.darkMode),
    ActivityLifecycleCallbacks by CurrentActivityObserver(context) {

    init {
        AppCompatDelegate.setDefaultNightMode(mainPrefs.darkMode.get().appCompatValue)
    }

    override fun onPrefsChanged(value: DarkMode) {
        AppCompatDelegate.setDefaultNightMode(value.appCompatValue)
    }

}

