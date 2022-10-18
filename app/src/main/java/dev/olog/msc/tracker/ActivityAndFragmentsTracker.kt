package dev.olog.msc.tracker

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import dev.olog.analytics.TrackerFacade
import javax.inject.Inject

class ActivityAndFragmentsTracker @Inject constructor(
    private val trackerFacade: TrackerFacade
) : Application.ActivityLifecycleCallbacks {

    private val fragmentObserver = object : FragmentManager.FragmentLifecycleCallbacks(){
        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            trackerFacade.trackScreen(f::class.java.simpleName, f.arguments)
        }
    }

    override fun onActivityResumed(activity: Activity) {
        (activity as? FragmentActivity)
            ?.supportFragmentManager
            ?.registerFragmentLifecycleCallbacks(fragmentObserver, true)
    }

    override fun onActivityPaused(activity: Activity) {
        (activity as? FragmentActivity)
            ?.supportFragmentManager
            ?.unregisterFragmentLifecycleCallbacks(fragmentObserver)
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

}