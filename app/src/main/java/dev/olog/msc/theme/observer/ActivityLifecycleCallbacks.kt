package dev.olog.msc.theme.observer

import android.app.Activity
import android.app.Application
import android.os.Bundle

internal interface ActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

    val currentActivity: Activity?

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, savedInstanceState: Bundle) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivityCreated(activity: Activity, p1: Bundle?) {
    }

    override fun onActivityResumed(activity: Activity) {
    }
}