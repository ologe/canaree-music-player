package dev.olog.presentation.service_music

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import dev.olog.shared.ApplicationContext
import dev.olog.shared.ProcessLifecycle
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicServiceController @Inject constructor(
        @ApplicationContext private val context: Context,
        @ProcessLifecycle lifecycle: Lifecycle,
        private val serviceClass: MusicServiceBinder

) : DefaultLifecycleObserver {

    init {
        lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        val intent = Intent(context, serviceClass.get())
        intent.action = "activity.start_service"
        ContextCompat.startForegroundService(context, intent)
    }

    override fun onStop(owner: LifecycleOwner) {
        val intent = Intent(context, serviceClass.get())
        intent.action = "activity.stop_service"
        ContextCompat.startForegroundService(context, intent)
    }

}