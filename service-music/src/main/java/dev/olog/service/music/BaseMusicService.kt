package dev.olog.service.music

import android.app.Service
import android.content.Intent
import android.os.Debug
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.media.MediaBrowserServiceCompat
import dev.olog.service.music.interfaces.Player
import dev.olog.service.music.interfaces.ServiceLifecycleController
import dev.olog.shared.MusicConstants
import dev.olog.shared.MusicServiceAction
import dev.olog.shared.PendingIntents
import javax.inject.Inject

abstract class BaseMusicService : MediaBrowserServiceCompat(),
    LifecycleOwner,
    ServiceLifecycleController {

    companion object {
        private const val ACTION_KEEP_SERVICE_ALIVE = "action.KEEP_SERVICE_ALIVE"
    }

    @Suppress("LeakingThis")
    private val dispatcher = ServiceLifecycleDispatcher(this)

    @Inject
    lateinit var player: Player

    private var serviceStarted = false

    @CallSuper
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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
        intent ?: return

        if (intent.action == null){
            Log.w("MusicService", "Service started with null action, killing")
            stop()
            return
        }

        val musicServiceAction = MusicServiceAction.values().find { it.name == intent.action }

        when (musicServiceAction){
            MusicServiceAction.SHUFFLE -> handleAppShortcutShuffle(intent)
            MusicServiceAction.PLAY -> handleAppShortcutPlay(intent)
            MusicServiceAction.PLAY_URI -> handlePlayFromUri(intent)
            MusicServiceAction.PLAY_PAUSE -> handlePlayPause(intent)
            MusicServiceAction.SKIP_NEXT -> handleSkipNext(intent)
            MusicServiceAction.SKIP_PREVIOUS -> handleSkipPrevious(intent)
            MusicServiceAction.TOGGLE_FAVORITE -> handleToggleFavorite()
            MusicServiceAction.FORWARD_10 -> handleForward10(intent)
            MusicServiceAction.FORWARD_30 -> handleForward30(intent)
            MusicServiceAction.REPLAY_10 -> handleReplay10(intent)
            MusicServiceAction.REPLAY_30 -> handleReplay30(intent)
        }

        when (intent.action) {
            null -> stop()
            ACTION_KEEP_SERVICE_ALIVE -> {
            }
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

    override fun getLifecycle(): Lifecycle = dispatcher.lifecycle

    protected abstract fun handleMediaButton(intent: Intent)

}