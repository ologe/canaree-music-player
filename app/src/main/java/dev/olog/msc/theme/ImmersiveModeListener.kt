package dev.olog.msc.theme

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.msc.R
import dev.olog.msc.theme.observer.ActivityLifecycleCallbacks
import dev.olog.msc.theme.observer.CurrentActivityObserver
import dev.olog.shared.widgets.StatusBarView
import dev.olog.shared.ConflatedSharedFlow
import dev.olog.shared.android.theme.ImmersiveAmbient
import dev.olog.shared.value
import javax.inject.Inject

internal class ImmersiveModeListener @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: SharedPreferences
) : BaseThemeUpdater(
    key = context.getString(R.string.prefs_immersive_key)
), ActivityLifecycleCallbacks by CurrentActivityObserver(context),
    ImmersiveAmbient {

    private val _publisher = ConflatedSharedFlow(fetchValue())

    init {
        prefs.registerOnSharedPreferenceChangeListener(this)
    }

    override val isEnabled: Boolean
        get() = _publisher.value

    override fun onPrefsChanged() {
        _publisher.tryEmit(fetchValue())

        StatusBarView.viewHeight = -1
        currentActivity?.recreate()
    }

    private fun fetchValue(): Boolean {
        return prefs.getBoolean(key, false)
    }

}