package dev.olog.service.music

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.service.music.EventDispatcher.Event
import javax.inject.Inject

@ServiceScoped
internal class Noisy @Inject constructor(
    private val service: Service,
    private val eventDispatcher: EventDispatcher

) : DefaultLifecycleObserver {

    private val noisyFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)

    private var registered: Boolean = false

    override fun onDestroy(owner: LifecycleOwner) {
        unregister()
    }

    fun register() {
        if (registered){
            return
        }
        service.registerReceiver(receiver, noisyFilter)
        registered = true
    }

    fun unregister() {
        if (!registered) {
            return
        }

        service.unregisterReceiver(receiver)
        registered = false
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
                eventDispatcher.dispatchEvent(Event.PLAY_PAUSE)
            }

        }
    }

}
