package dev.olog.analytics.tracker

import android.os.Bundle
import com.crashlytics.android.Crashlytics
import dev.olog.analytics.TrackerFacade
import dev.olog.core.schedulers.Schedulers
import dev.olog.shared.launchUnit
import kotlinx.coroutines.GlobalScope
import timber.log.Timber
import javax.inject.Inject

internal class FirebaseTracker @Inject constructor(
    private val schedulers: Schedulers
) : TrackerFacade {

    override fun trackScreen(
        name: String,
        bundle: Bundle?
    ) = GlobalScope.launchUnit(schedulers.io) {
        try {
            Crashlytics.log("screen=$name, arguments=${bundle?.toMap()}}")
        } catch (ex: Exception) {
            Timber.w(ex, "screen=$name")
        }
    }

    override fun trackServiceEvent(
        name: String,
        vararg args: Any?
    ) = GlobalScope.launchUnit(schedulers.io) {
        try {
            Crashlytics.log("service event=$name, arguments=$args")
        } catch (ex: Exception) {
            Timber.w(ex, "service event $name")
        }
    }

    private fun Bundle.toMap(): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()

        for (s in keySet()) {
            result[s] = get(s).toString()
        }

        return result
    }


}