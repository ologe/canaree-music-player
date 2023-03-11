package dev.olog.msc.theme

import android.app.Application
import android.content.SharedPreferences
import dev.olog.msc.theme.observer.ActivityLifecycleCallbacks
import dev.olog.msc.theme.observer.CurrentActivityObserver
import dev.olog.presentation.R
import dev.olog.presentation.widgets.StatusBarView
import dev.olog.shared.mutableLazy
import javax.inject.Inject

internal class ImmersiveModeListener @Inject constructor(
    application: Application,
    prefs: SharedPreferences
) : BaseThemeUpdater<Boolean>(application, prefs, application.getString(R.string.prefs_immersive_key)),
    ActivityLifecycleCallbacks by CurrentActivityObserver(application) {

    var isImmersive by mutableLazy { getValue() }
        private set

    override fun onPrefsChanged() {
        StatusBarView.viewHeight = -1
        isImmersive = getValue()
        currentActivity?.recreate()
    }

    override fun getValue(): Boolean {
        return prefs.getBoolean(key, false)
    }

}