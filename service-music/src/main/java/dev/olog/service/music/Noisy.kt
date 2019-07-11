package dev.olog.service.music

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.view.KeyEvent.KEYCODE_MEDIA_PAUSE
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dagger.Lazy
import dev.olog.injection.dagger.PerService
import dev.olog.injection.dagger.ServiceContext
import dev.olog.injection.dagger.ServiceLifecycle
import dev.olog.shared.extensions.dispatchEvent
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

            if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
                audioManager.get().dispatchEvent(KEYCODE_MEDIA_PAUSE)
            }

        }
    }

}
