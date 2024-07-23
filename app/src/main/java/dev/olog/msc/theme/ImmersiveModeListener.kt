package dev.olog.msc.theme

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.msc.theme.observer.ActivityLifecycleCallbacks
import dev.olog.msc.theme.observer.CurrentActivityObserver
import dev.olog.presentation.R
import dev.olog.presentation.widgets.StatusBarView
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

internal class ImmersiveModeListener @Inject constructor(
    @ApplicationContext context: Context,
    prefs: SharedPreferences
) : BaseThemeUpdater<Boolean>(context, prefs, context.getString(R.string.prefs_immersive_key)),
    ActivityLifecycleCallbacks by CurrentActivityObserver(context) {

    private val _flow by lazy { MutableStateFlow(getValue()) }
    val isImmersive: Boolean
        get() = _flow.value

    override fun onPrefsChanged() {
        StatusBarView.viewHeight = -1
        _flow.value = getValue()
        currentActivity?.recreate() // TODO are there alternatives?
    }

    override fun getValue(): Boolean {
        return prefs.getBoolean(key, false)
    }

}