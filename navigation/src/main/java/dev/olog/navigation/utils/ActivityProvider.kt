package dev.olog.navigation.utils

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityProvider @Inject constructor(
    application: Application
) {

    companion object {
        private const val TAG = "ActivityProvider"
    }

    operator fun invoke(): FragmentActivity? {
        val result = currentActivity?.get()
        if (result == null) {
            Log.w(TAG, "Activity is null")
        }
        return result
    }

    private var currentActivity: WeakReference<FragmentActivity>? = null

    init {
        application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {

            override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
                keepSafeReference(activity)
            }


            override fun onActivityResumed(activity: Activity) {
                keepSafeReference(activity)
            }

            override fun onActivityPaused(activity: Activity) {
                currentActivity = null
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityDestroyed(activity: Activity) {}

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityStopped(activity: Activity) {}

            private fun keepSafeReference(activity: Activity) {
                if (activity is FragmentActivity) {
                    currentActivity = WeakReference(activity)
                } else {
                    Log.w(TAG, "Activity $activity is not extending FragmentActivity")
                }
            }

        })
    }

}