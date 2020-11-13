package dev.olog.analytics.tracker

import android.os.Bundle
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import dev.olog.analytics.TrackerFacade
import dev.olog.shared.launchUnit
import kotlinx.coroutines.GlobalScope
import javax.inject.Inject

internal class FirebaseTracker @Inject constructor(
    private val firebase: FirebaseAnalytics
) : TrackerFacade {

    override fun trackScreen(name: String, bundle: Bundle?) = GlobalScope.launchUnit {
        try {
            firebase.logEvent(name.take(40), bundle)
        } catch (ignored: Throwable) {
        }
    }

    override fun trackServiceEvent(name: String, vararg args: Any?) = GlobalScope.launchUnit {
        try {
            val map = args
                .mapIndexed { index, any -> "arg$index" to any }
                .toTypedArray()

            firebase.logEvent(name.take(40), bundleOf(*map)) // TODO need to use crashlytics, not analytics
        } catch (ignored: Throwable) {
        }
    }


}