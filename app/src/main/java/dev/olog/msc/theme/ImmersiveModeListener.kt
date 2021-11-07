package dev.olog.msc.theme

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.feature.main.MainPrefs
import dev.olog.msc.theme.observer.ActivityLifecycleCallbacks
import dev.olog.msc.theme.observer.CurrentActivityObserver
import dev.olog.shared.widgets.StatusBarView
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

internal class ImmersiveModeListener @Inject constructor(
    @ApplicationContext context: Context,
    appScope: CoroutineScope,
    mainPrefs: MainPrefs,
) : BaseThemeUpdater<Boolean>(appScope, mainPrefs.immersiveMode),
    ActivityLifecycleCallbacks by CurrentActivityObserver(context) {

    var isImmersive: Boolean = mainPrefs.immersiveMode.get()
        private set

    override fun onPrefsChanged(value: Boolean) {
        StatusBarView.viewHeight = -1
        isImmersive = value
        currentActivity?.recreate()
    }

}