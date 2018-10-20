package dev.olog.msc.music.service

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
import dev.olog.msc.dagger.qualifier.ServiceContext
import dev.olog.msc.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.dagger.scope.PerService
import dev.olog.msc.utils.k.extension.dispatchEvent
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

            if (intent.action?.isBecomingNoisy() == true) {
                audioManager.get().dispatchEvent(KEYCODE_MEDIA_PAUSE)
            }

        }
    }

}

private fun String.isBecomingNoisy() : Boolean{
    return this == AudioManager.ACTION_AUDIO_BECOMING_NOISY
}
