package dev.olog.service.music

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.service.music.EventDispatcher.Event
import dev.olog.shared.autoDisposeJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@ServiceScoped
internal class MediaButton @Inject internal constructor(
    private val lifecycleOwner: LifecycleOwner,
    private val eventDispatcher: EventDispatcher
) {

    companion object {
        internal const val DELAY = 300L
        internal const val MAX_ALLOWED_CLICKS = 3
    }

    private var clicks = 0

    private var job by autoDisposeJob()

    fun onHeatSetHookClick() {
        clicks++

        if (clicks <= MAX_ALLOWED_CLICKS) {
            job = lifecycleOwner.lifecycleScope.launch {
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
