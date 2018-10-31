package dev.olog.msc.analytics

import android.app.Activity
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import dev.olog.msc.app.app
import dev.olog.msc.presentation.base.LoggableFragment
import dev.olog.msc.presentation.base.LoggableService
import dev.olog.msc.presentation.utils.lazyFast
import java.util.concurrent.TimeUnit

object AppAnalytics {

    private const val SERVICE_TIME_USED = "SERVICE_MINUTES_USED"
    private const val SERVICE_TIME = "SERVICE_MINUTES"

    private val serviceTimeTrack = mutableMapOf<String, Long>()

    private val firebase by lazyFast { FirebaseAnalytics.getInstance(app) }

    fun logScreen(activity: Activity?, fragment: LoggableFragment){
        activity?.let {
            val fragmentName = fragment::class.java.simpleName
            firebase.setCurrentScreen(it, fragmentName, fragmentName)
        }
    }

    fun trackServiceStart(clazz: LoggableService){
        serviceTimeTrack[clazz::class.java.simpleName] = System.currentTimeMillis()
    }

    fun trackServiceEnd(clazz: LoggableService){
        val startTime = serviceTimeTrack[clazz::class.java.simpleName] ?: System.currentTimeMillis()
        val timeSpent = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - startTime)
        firebase.logEvent(SERVICE_TIME_USED, bundleOf(SERVICE_TIME to timeSpent))
    }

}