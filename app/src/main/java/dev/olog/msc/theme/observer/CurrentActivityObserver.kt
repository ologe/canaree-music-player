package dev.olog.msc.theme.observer

import android.app.Activity
import android.app.Application
import android.content.Context
import dev.olog.shared.android.extensions.findInContext

internal class CurrentActivityObserver(context: Context) :
    ActivityLifecycleCallbacks {

    override var currentActivity: Activity? = null
        private set

    init {
        (context.applicationContext.findInContext<Application>()).registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityStopped(activity: Activity) {
        currentActivity = null
    }

}