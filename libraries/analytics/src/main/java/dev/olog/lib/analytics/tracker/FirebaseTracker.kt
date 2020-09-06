package dev.olog.lib.analytics.tracker

import android.os.Bundle
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dev.olog.lib.analytics.TrackerFacade
import dev.olog.domain.schedulers.Schedulers
import dev.olog.shared.coroutines.fireAndForget
import kotlinx.coroutines.GlobalScope
import timber.log.Timber
import javax.inject.Inject

internal class FirebaseTracker @Inject constructor(
    private val schedulers: Schedulers
) : TrackerFacade {

    // TODO inject?
    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun trackScreen(
        name: String,
        bundle: Bundle?
    ) = GlobalScope.fireAndForget(schedulers.io) {
        try {
            crashlytics.log("screen=$name, arguments=${bundle?.toMap()}}")
        } catch (ex: Exception) {
            Timber.w(ex, "screen=$name")
        }
    }

    override fun trackServiceEvent(
        name: String,
        vararg args: Any?
    ) = GlobalScope.fireAndForget(schedulers.io) {
        try {
            crashlytics.log("service event=$name, arguments=$args")
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