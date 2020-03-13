package dev.olog.analytics.tracker

import android.os.Bundle
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import dev.olog.analytics.TrackerFacade
import dev.olog.core.schedulers.Schedulers
import dev.olog.shared.launchUnit
import kotlinx.coroutines.GlobalScope
import timber.log.Timber
import javax.inject.Inject

internal class FirebaseTracker @Inject constructor(
    private val firebase: FirebaseAnalytics,
    private val schedulers: Schedulers
) : TrackerFacade {

    companion object {
        internal const val MAX_SIZE_ALLOWED = 40
    }

    override fun trackScreen(
        name: String,
        bundle: Bundle?
    ) = GlobalScope.launchUnit(schedulers.io) {
        try {
            firebase.logEvent(name.take(MAX_SIZE_ALLOWED), bundle)
        } catch (ex: Exception) {
            Timber.w(ex, "screen $name")
        }
    }

    override fun trackServiceEvent(
        name: String,
        vararg args: Any?
    ) = GlobalScope.launchUnit(schedulers.io) {
        try {
            val map = args
                .mapIndexed { index, any -> "arg$index" to any.toString() }
                .toTypedArray()

            firebase.logEvent(name.take(MAX_SIZE_ALLOWED), bundleOf(*map))
        } catch (ex: Exception) {
            Timber.w(ex, "service event $name")
        }
    }


}