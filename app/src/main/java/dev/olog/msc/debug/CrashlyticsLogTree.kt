package dev.olog.msc.debug

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class CrashlyticsLogTree : Timber.Tree() {

    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?
    ) {
        when (priority) {
            Log.WARN,
            Log.ERROR -> {
                // TODO check
                t?.let { FirebaseCrashlytics.getInstance().log(it.stackTraceToString()) }
                FirebaseCrashlytics.getInstance().log(message)
            }
        }
    }

}