package dev.olog.service.music

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dev.olog.injection.dagger.PerService
import dev.olog.injection.dagger.ServiceContext
import dev.olog.service.music.EventDispatcher.Event
import javax.inject.Inject

@PerService
internal class Noisy @Inject constructor(
    @ServiceContext private val context: Context,
    private val eventDispatcher: EventDispatcher

) : DefaultLifecycleObserver {

    private val noisyFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)

    private var registered: Boolean = false

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
                eventDispatcher.dispatchEvent(Event.PLAY_PAUSE)
            }

        }
    }

}
