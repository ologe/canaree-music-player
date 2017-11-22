package dev.olog.music_service

import android.app.Service
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ServiceLifecycleDispatcher
import android.content.Intent
import android.support.annotation.CallSuper
import android.support.v4.content.ContextCompat
import android.support.v4.media.MediaBrowserServiceCompat
import dagger.android.AndroidInjection

abstract class BaseMusicService : MediaBrowserServiceCompat(),
        LifecycleOwner,
        ServiceLifecycleController {

    companion object {
        private const val ACTION_KEEP_SERVICE_ALIVE = "action.KEEP_SERVICE_ALIVE"
    }

    @Suppress("LeakingThis")
    private val dispatcher = ServiceLifecycleDispatcher(this)

    private var serviceStarted = false

    @CallSuper
    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    @CallSuper
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        serviceStarted = true

        handleIntent(intent)

        return Service.START_NOT_STICKY
    }

    @CallSuper
    override fun onDestroy() {
        dispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) return

        val action = intent.action
        if (action == null) {
            stop()
        } else {
            handleMediaButton(intent)
        }
    }

    override fun start() {
        if (!serviceStarted) {
            val intent = Intent(this, javaClass)
            intent.action = ACTION_KEEP_SERVICE_ALIVE
            ContextCompat.startForegroundService(this, intent)
            serviceStarted = true
        }
    }

    override fun stop() {
        if (serviceStarted) {
            serviceStarted = false
            stopSelf()
        }
    }

    override fun getLifecycle(): Lifecycle = dispatcher.lifecycle

    protected abstract fun handleMediaButton(intent: Intent)

}