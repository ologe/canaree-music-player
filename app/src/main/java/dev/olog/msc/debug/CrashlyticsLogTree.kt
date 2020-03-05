package dev.olog.msc.debug

import android.util.Log
import com.crashlytics.android.Crashlytics
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
                t?.let { Crashlytics.logException(it) }
                Crashlytics.log(message)
            }
        }
    }

}