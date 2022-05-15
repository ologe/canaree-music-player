package dev.olog.feature.media

import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.core.ServiceScope
import dev.olog.feature.media.EventDispatcher.Event
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@ServiceScoped
internal class MediaButton @Inject internal constructor(
    private val eventDispatcher: EventDispatcher,
    private val serviceScope: ServiceScope,
) {

    companion object {
        internal const val DELAY = 300L
        internal const val MAX_ALLOWED_CLICKS = 3
    }

    private var clicks = 0

    private var job: Job? = null

    fun onHeatSetHookClick() {
        clicks++

        if (clicks <= MAX_ALLOWED_CLICKS) {
            job?.cancel()
            job = serviceScope.launch {
                delay(DELAY)
                dispatchEvent(clicks)
                clicks = 0
            }
        }
    }

    private fun dispatchEvent(clicks: Int) {
        when (clicks) {
            0 -> {
            }
            1 -> eventDispatcher.dispatchEvent(Event.PLAY_PAUSE)
            2 -> eventDispatcher.dispatchEvent(Event.SKIP_NEXT)
            3 -> eventDispatcher.dispatchEvent(Event.SKIP_PREVIOUS)
        }
    }

}
