package dev.olog.service.music

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.media.MediaBrowserServiceCompat
import dev.olog.core.interactor.SleepTimerUseCase
import dev.olog.intents.MusicServiceAction
import dev.olog.intents.MusicServiceCustomAction
import dev.olog.service.music.interfaces.IPlayer
import dev.olog.service.music.interfaces.IServiceLifecycleController
import javax.inject.Inject

abstract class BaseMusicService : LifecycleMediaBrowserServiceCompat(),
    IServiceLifecycleController {

    companion object {
        private const val ACTION_KEEP_SERVICE_ALIVE = "action.KEEP_SERVICE_ALIVE"
    }

    @Inject
    internal lateinit var player: IPlayer

    private var serviceStarted = false

    @CallSuper
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        serviceStarted = true

        handleIntent(intent)

        return Service.START_NOT_STICKY
    }

    private fun handleIntent(intent: Intent?) {
        intent ?: return

        if (intent.action == null){
            Log.w("MusicService", "Service started with null action, killing")
            stop()
            return
        }

        val musicServiceAction = MusicServiceAction.values().find { it.name == intent.action }
        val musicServiceCustomAction = MusicServiceCustomAction.values().find { it.name == intent.action }


        when (musicServiceAction){
            MusicServiceAction.PLAY -> handleAppShortcutPlay(intent)
            MusicServiceAction.PLAY_URI -> handlePlayFromUri(intent)
            MusicServiceAction.PLAY_PAUSE -> handlePlayPause(intent)
            MusicServiceAction.SKIP_NEXT -> handleSkipNext(intent)
            MusicServiceAction.SKIP_PREVIOUS -> handleSkipPrevious(intent)
        }

        when (musicServiceCustomAction){
            MusicServiceCustomAction.SHUFFLE -> handleAppShortcutShuffle(intent)
            MusicServiceCustomAction.FORWARD_10 -> handleForward10(intent)
            MusicServiceCustomAction.FORWARD_30 -> handleForward30(intent)
            MusicServiceCustomAction.REPLAY_10 -> handleReplay10(intent)
            MusicServiceCustomAction.REPLAY_30 -> handleReplay30(intent)
            MusicServiceCustomAction.TOGGLE_FAVORITE -> handleToggleFavorite()
            else -> {}
        }

        when (intent.action) {
            null -> stop()
            ACTION_KEEP_SERVICE_ALIVE -> {
            }
            SleepTimerUseCase.ACTION_STOP_SLEEP_END -> handleSleepTimerEnd(intent)
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
    protected abstract fun handleToggleFavorite()
    protected abstract fun handlePlayFromUri(intent: Intent)
    protected abstract fun handleReplay10(intent: Intent)
    protected abstract fun handleReplay30(intent: Intent)
    protected abstract fun handleForward10(intent: Intent)
    protected abstract fun handleForward30(intent: Intent)

    override fun start() {
        if (!serviceStarted) {
            val intent = Intent(this, MusicService::class.java)
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

    protected abstract fun handleMediaButton(intent: Intent)

}

abstract class LifecycleMediaBrowserServiceCompat : MediaBrowserServiceCompat(), LifecycleOwner {

    @Suppress("LeakingThis")
    private val dispatcher = ServiceLifecycleDispatcher(this)

    @CallSuper
    override fun onCreate() {
        dispatcher.onServicePreSuperOnCreate()
        super.onCreate()
    }

    @CallSuper
    override fun onBind(intent: Intent): IBinder? {
        dispatcher.onServicePreSuperOnBind()
        return super.onBind(intent)
    }

    @Suppress("deprecation")
    @CallSuper
    override fun onStart(intent: Intent?, startId: Int) {
        dispatcher.onServicePreSuperOnStart()
        super.onStart(intent, startId)
    }

    // this method is added only to annotate it with @CallSuper.
    // In usual service super.onStartCommand is no-op, but in LifecycleService
    // it results in mDispatcher.onServicePreSuperOnStart() call, because
    // super.onStartCommand calls onStart().
    @CallSuper
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    @CallSuper
    override fun onDestroy() {
        dispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()
    }

    override fun getLifecycle(): Lifecycle {
        return dispatcher.lifecycle
    }

}