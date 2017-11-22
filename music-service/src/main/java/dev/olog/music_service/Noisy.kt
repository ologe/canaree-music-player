package dev.olog.music_service

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.view.KeyEvent.KEYCODE_MEDIA_PAUSE
import dagger.Lazy
import dev.olog.music_service.di.PerService
import dev.olog.music_service.di.ServiceContext
import dev.olog.music_service.di.ServiceLifecycle
import dev.olog.music_service.utils.dispatchEvent
import javax.inject.Inject

@PerService
class Noisy @Inject constructor(
        @ServiceContext private val context: Context,
        @ServiceLifecycle lifecycle: Lifecycle,
        private val audioManager: Lazy<AudioManager>

) : DefaultLifecycleObserver {

    private val noisyFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)

    private var registered: Boolean = false

    init {
        lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        unregister()
    }

    fun register() {
        if (!registered) {
            context.registerReceiver(receiver, noisyFilter)
            registered = true
        }
    }

    fun unregister() {
        if (registered) {
            context.unregisterReceiver(receiver)
            registered = false
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            if (intent.action.isBecomingNoisy()) {
                audioManager.get().dispatchEvent(KEYCODE_MEDIA_PAUSE)
            }

        }
    }

}

private fun String.isBecomingNoisy() : Boolean{
    return this == AudioManager.ACTION_AUDIO_BECOMING_NOISY
}
