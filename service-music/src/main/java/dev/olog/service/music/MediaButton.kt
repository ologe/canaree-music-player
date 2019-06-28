package dev.olog.service.music

import android.content.Intent
import android.media.AudioManager
import android.view.KeyEvent
import android.view.KeyEvent.*
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dagger.Lazy
import dev.olog.injection.dagger.PerService
import dev.olog.injection.dagger.ServiceLifecycle
import dev.olog.shared.extensions.dispatchEvent
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject


@PerService
class MediaButton @Inject internal constructor(
    @ServiceLifecycle lifecycle: Lifecycle,
    private val audioManager: Lazy<AudioManager>

) : DefaultLifecycleObserver, CoroutineScope by MainScope() {

    private var clicks = AtomicInteger(0)

    private var job: Job? = null

    init {
        lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        job?.cancel()
    }

    fun onNextEvent(mediaButtonEvent: Intent) {
        val event = mediaButtonEvent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)

        if (event.action == ACTION_DOWN && event.keyCode == KEYCODE_HEADSETHOOK) {
            val current = clicks.incrementAndGet()

            if (current < 5) {
                job?.cancel()
                job = launch { // TODO check if works
                    delay(300)
                    dispatchEvent(current)
                }
            }
        }
    }

    private fun dispatchEvent(clicks: Int) {
        when (clicks) {
            0 -> {
            }
            1 -> audioManager.get().dispatchEvent(KEYCODE_MEDIA_PLAY_PAUSE)
            2 -> audioManager.get().dispatchEvent(KEYCODE_MEDIA_NEXT)
            3 -> audioManager.get().dispatchEvent(KEYCODE_MEDIA_PREVIOUS)
//            else -> speech.speak()
        }
        this.clicks.set(0)
    }

}
