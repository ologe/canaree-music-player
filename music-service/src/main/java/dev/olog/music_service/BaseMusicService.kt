package dev.olog.music_service

import android.app.Service
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ServiceLifecycleDispatcher
import android.content.Intent
import android.provider.MediaStore
import android.support.annotation.CallSuper
import android.support.v4.content.ContextCompat
import android.support.v4.media.MediaBrowserServiceCompat
import dagger.android.AndroidInjection
import dev.olog.music_service.interfaces.Player
import dev.olog.music_service.interfaces.ServiceLifecycleController
import dev.olog.shared_android.Constants
import dev.olog.shared_android.PendingIntents
import javax.inject.Inject

abstract class BaseMusicService : MediaBrowserServiceCompat(),
        LifecycleOwner,
        ServiceLifecycleController {

    companion object {
        private const val ACTION_KEEP_SERVICE_ALIVE = "action.KEEP_SERVICE_ALIVE"
    }

    @Suppress("LeakingThis")
    private val dispatcher = ServiceLifecycleDispatcher(this)

    @Inject lateinit var player: Player

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

        when (action){
            null -> stop()
            Constants.SHORTCUT_SHUFFLE -> handleAppShortcutShuffle(intent)
            Constants.SHORTCUT_PLAY -> handleAppShortcutPlay(intent)
            Constants.WIDGET_ACTION_PLAY_PAUSE -> handlePlayPause(intent)
            Constants.WIDGET_ACTION_SKIP_NEXT -> handleSkipNext(intent)
            Constants.WIDGET_ACTION_SKIP_PREVIOUS -> handleSkipPrevious(intent)
            PendingIntents.ACTION_STOP_SLEEP_END -> handleSleepTimerEnd(intent)
            MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH -> handlePlayFromVoiceSearch(intent)
            else -> handleMediaButton(intent)
        }
    }

    protected abstract fun handleAppShortcutPlay(intent: Intent)
    protected abstract fun handleAppShortcutShuffle(intent: Intent)
    protected abstract fun handlePlayPause(intent: Intent)
    protected abstract fun handleSkipNext(intent: Intent)
    protected abstract fun handleSkipPrevious(intent: Intent)
    protected abstract fun handleSleepTimerEnd(intent: Intent)
    protected abstract fun handlePlayFromVoiceSearch(intent: Intent)

    override fun start() {
        if (!serviceStarted) {
            val intent = Intent(this, this::class.java)
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