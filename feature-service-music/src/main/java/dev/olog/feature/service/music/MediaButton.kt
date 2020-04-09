package dev.olog.feature.service.music

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import dev.olog.core.dagger.FeatureScope
import dev.olog.core.dagger.ServiceLifecycle
import dev.olog.feature.service.music.EventDispatcher.Event
import dev.olog.shared.coroutines.autoDisposeJob
import kotlinx.coroutines.delay
import timber.log.Timber
import javax.inject.Inject


@FeatureScope
internal class MediaButton @Inject internal constructor(
    @ServiceLifecycle private val lifecycle: Lifecycle,
    private val eventDispatcher: EventDispatcher

) {

    companion object {
        @JvmStatic
        private val TAG = "SM:${MediaButton::class.java.simpleName}"
        internal const val DELAY = 300L
        internal const val MAX_ALLOWED_CLICKS = 3
    }

    private var clicks = 0

    private var job by autoDisposeJob()

    fun onHeatSetHookClick() {
        Timber.v("$TAG onHeatSetHookClick")
        clicks++

        if (clicks <= MAX_ALLOWED_CLICKS) {
            job = lifecycle.coroutineScope.launchWhenResumed {
                delay(DELAY)
                dispatchEvent(clicks)
                clicks = 0
            }
        }
    }

    private fun dispatchEvent(clicks: Int) {
        Timber.v("$TAG dispatchEvent clicks=$clicks")

        when (clicks) {
            0 -> {
            }
            1 -> eventDispatcher.dispatchEvent(Event.PLAY_PAUSE)
            2 -> eventDispatcher.dispatchEvent(Event.SKIP_NEXT)
            3 -> eventDispatcher.dispatchEvent(Event.SKIP_PREVIOUS)
        }
    }

}
