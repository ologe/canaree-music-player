package dev.olog.feature.media.impl

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.feature.media.impl.EventDispatcher.Event
import dev.olog.platform.extension.lifecycleOwner
import javax.inject.Inject

@ServiceScoped
class Noisy @Inject constructor(
    private val service: Service,
    private val eventDispatcher: EventDispatcher

) : DefaultLifecycleObserver {

    companion object {
        private val TAG = "SM:${Noisy::class.java.simpleName}"
    }

    private val noisyFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)

    private var registered: Boolean = false

    init {
        service.lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        unregister()
    }

    fun register() {
        if (registered){
            Log.w(TAG, "trying to re-register")
            return
        }
        Log.v(TAG, "register")
        service.registerReceiver(receiver, noisyFilter)
        registered = true
    }

    fun unregister() {
        if (!registered) {
            Log.w(TAG, "trying to unregister but never registered")
            return
        }

        Log.v(TAG, "unregister")
        service.unregisterReceiver(receiver)
        registered = false
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
                Log.v(TAG, "on receiver noisy broadcast")
                eventDispatcher.dispatchEvent(Event.PLAY_PAUSE)
            }

        }
    }

}
