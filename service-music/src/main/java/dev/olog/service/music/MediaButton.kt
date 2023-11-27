package dev.olog.service.music

import android.util.Log
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.service.music.EventDispatcher.Event
import kotlinx.coroutines.*
import javax.inject.Inject


@ServiceScoped
internal class MediaButton @Inject internal constructor(
    private val eventDispatcher: EventDispatcher

) : CoroutineScope by MainScope() {

    companion object {
        @JvmStatic
        private val TAG = "SM:${MediaButton::class.java.simpleName}"
        internal const val DELAY = 300L
        internal const val MAX_ALLOWED_CLICKS = 3
    }

    private var clicks = 0

    private var job: Job? = null

    fun onHeatSetHookClick() {
        Log.v(TAG, "onHeatSetHookClick")
        clicks++

        if (clicks <= MAX_ALLOWED_CLICKS) {
            job?.cancel()
            job = launch {
                delay(DELAY)
                dispatchEvent(clicks)
                clicks = 0
            }
        }
    }

    private fun dispatchEvent(clicks: Int) {
        Log.v(TAG, "dispatchEvent clicks=$clicks")

        when (clicks) {
            0 -> {
            }
            1 -> eventDispatcher.dispatchEvent(Event.PLAY_PAUSE)
            2 -> eventDispatcher.dispatchEvent(Event.SKIP_NEXT)
            3 -> eventDispatcher.dispatchEvent(Event.SKIP_PREVIOUS)
        }
    }

}
