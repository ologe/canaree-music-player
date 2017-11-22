package dev.olog.music_service

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Intent
import android.media.AudioManager
import android.os.Handler
import android.view.KeyEvent
import android.view.KeyEvent.*
import dagger.Lazy
import dev.olog.music_service.di.PerService
import dev.olog.music_service.di.ServiceLifecycle
import dev.olog.music_service.utils.dispatchEvent
import javax.inject.Inject


@PerService
class MediaButton @Inject internal constructor(
        @ServiceLifecycle lifecycle: Lifecycle,
        private val audioManager: Lazy<AudioManager>

) : DefaultLifecycleObserver {

    private var handler: Handler = Handler()
    private var clicks = 0

    private val runnable = {
        when (clicks) {
            1 -> audioManager.get().dispatchEvent(KEYCODE_MEDIA_PLAY_PAUSE)
            2 -> audioManager.get().dispatchEvent(KEYCODE_MEDIA_NEXT)
            3 -> audioManager.get().dispatchEvent(KEYCODE_MEDIA_PREVIOUS)
        }// TODO speech
        clicks = 0
    }

    init {
        lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        handler.removeCallbacks(runnable)
    }

    fun onNextEvent(mediaButtonEvent: Intent) {
        val event = mediaButtonEvent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)


        if (event.action == ACTION_DOWN && event.keyCode == KEYCODE_HEADSETHOOK) {
            clicks++

            if (clicks < 5) {
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, 300)
            }
        }
    }

}
