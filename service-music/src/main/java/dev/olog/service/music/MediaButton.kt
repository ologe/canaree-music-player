package dev.olog.service.music

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.injection.dagger.PerService
import dev.olog.injection.dagger.ServiceLifecycle
import dev.olog.service.music.EventDispatcher.Event
import dev.olog.shared.autoDisposeJob
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject


@PerService
internal class MediaButton @Inject internal constructor(
    @ServiceLifecycle lifecycle: Lifecycle,
    private val eventDispatcher: EventDispatcher

) : DefaultLifecycleObserver, CoroutineScope by MainScope() {

    companion object {
        @JvmStatic
        private val TAG = "SM:${MediaButton::class.java.simpleName}"
        internal const val DELAY = 300L
        internal const val MAX_ALLOWED_CLICKS = 3
    }

    init {
        lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        cancel()
    }

    private var clicks = 0

    private var job by autoDisposeJob()

    fun onHeatSetHookClick() {
        Timber.v("$TAG onHeatSetHookClick")
        clicks++

        if (clicks <= MAX_ALLOWED_CLICKS) {
            job = launch {
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
