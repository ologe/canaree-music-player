package dev.olog.msc.analytics

import android.app.Activity
import com.google.firebase.analytics.FirebaseAnalytics
import dev.olog.msc.app.app
import dev.olog.msc.presentation.base.LoggableFragment
import dev.olog.msc.presentation.utils.lazyFast

object AppAnalytics {

    private val firebase by lazyFast { FirebaseAnalytics.getInstance(app) }

    fun logScreen(activity: Activity?, fragment: LoggableFragment){
        activity?.let {
            val fragmentName = fragment::class.java.simpleName
            firebase.setCurrentScreen(it, fragmentName, fragmentName)
        }
    }

}