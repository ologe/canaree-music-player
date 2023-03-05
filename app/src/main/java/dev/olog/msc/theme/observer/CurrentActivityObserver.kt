package dev.olog.msc.theme.observer

import android.app.Activity
import android.app.Application

internal class CurrentActivityObserver(application: Application) :
    ActivityLifecycleCallbacks {

    override var currentActivity: Activity? = null
        private set

    init {
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityStopped(activity: Activity) {
        currentActivity = null
    }

}