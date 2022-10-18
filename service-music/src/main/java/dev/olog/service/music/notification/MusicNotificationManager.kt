package dev.olog.service.music.notification

import android.app.Notification
import android.app.Service
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.core.entity.favorite.FavoriteEnum
import dev.olog.core.interactor.favorite.ObserveFavoriteAnimationUseCase
import dev.olog.service.music.interfaces.INotification
import dev.olog.service.music.interfaces.IPlayerLifecycle
import dev.olog.service.music.model.Event
import dev.olog.service.music.model.MediaEntity
import dev.olog.service.music.model.MetadataEntity
import dev.olog.service.music.model.MusicNotificationState
import dev.olog.shared.CustomScope
import dev.olog.shared.android.utils.isOreo
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@ServiceScoped
internal class MusicNotificationManager @Inject constructor(
    private val service: Service,
    private val notificationImpl: INotification,
    observeFavoriteUseCase: ObserveFavoriteAnimationUseCase,
    playerLifecycle: IPlayerLifecycle

) : DefaultLifecycleObserver, CoroutineScope by CustomScope() {

    companion object {
        @JvmStatic
        private val TAG = "SM:${MusicNotificationManager::class.java.simpleName}"
        private const val METADATA_PUBLISH_DELAY = 350L
        private const val STATE_PUBLISH_DELAY = 100L
        private const val FAVORITE_PUBLISH_DELAY = 100L
    }

    private var isForeground: Boolean = false

    private val publisher = Channel<Event>(Channel.UNLIMITED)
    private val currentState = MusicNotificationState()
    private var publishJob: Job? = null

    private val playerListener = object : IPlayerLifecycle.Listener {
        override fun onPrepare(metadata: MetadataEntity) {
            onNextMetadata(metadata.entity)
        }

        override fun onMetadataChanged(metadata: MetadataEntity) {
            onNextMetadata(metadata.entity)
        }

        override fun onStateChanged(state: PlaybackStateCompat) {
            onNextState(state)
        }
    }

    init {
        playerLifecycle.addListener(playerListener)

        launch {
            publisher.consumeAsFlow()
                .filter { event ->
                    when (event) {
                        is Event.Metadata -> currentState.isDifferentMetadata(event.entity)
                        is Event.State -> currentState.isDifferentState(event.state)
                        is Event.Favorite -> currentState.isDifferentFavorite(event.favorite)
                    }
                }.collect { consumeEvent(it) }
        }

        launch {
            observeFavoriteUseCase()
                .map { it == FavoriteEnum.FAVORITE }
                .collect { onNextFavorite(it) }
        }
    }

    private suspend fun consumeEvent(event: Event){
        Log.v(TAG, "on next event $event")

        publishJob?.cancel()
        when (event){
            is Event.Metadata -> {
                if (currentState.updateMetadata(event.entity)) {
                    publishNotification(currentState.deepCopy(), METADATA_PUBLISH_DELAY)
                }
            }
            is Event.State -> {
                if (currentState.updateState(event.state)){
                    publishNotification(currentState.deepCopy(), STATE_PUBLISH_DELAY)
                }
            }
            is Event.Favorite -> {
                if (currentState.updateFavorite(event.favorite)){
                    publishNotification(currentState.deepCopy(), FAVORITE_PUBLISH_DELAY)
                }
            }
        }
    }

    private suspend fun publishNotification(state: MusicNotificationState, delay: Long) {
        require(currentState !== state) // to avoid concurrency problems a copy is passed

        Log.v(TAG, "publish notification request with delay ${delay}ms, state=$state")
        if (!isForeground && isOreo()) {
            // oreo needs to post notification immediately after calling startForegroundService
            issueNotification(state)
        } else {
            // post delayed
            publishJob = GlobalScope.launch {
                delay(delay)
                issueNotification(state)
            }
        }
    }

    private suspend fun issueNotification(state: MusicNotificationState) {
        Log.v(TAG, "issue notification")
        val notification = notificationImpl.update(state)
        if (state.isPlaying) {
            startForeground(notification)
        } else {
            pauseForeground()
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        stopForeground()
        publishJob?.cancel()
        cancel()
    }

    private fun onNextMetadata(metadata: MediaEntity) {
        Log.v(TAG, "on next metadata=${metadata.title}")
        publisher.trySend(Event.Metadata(metadata))
    }

    private fun onNextState(playbackState: PlaybackStateCompat) {
        Log.v(TAG, "on next state")
        publisher.trySend(Event.State(playbackState))
    }

    private fun onNextFavorite(isFavorite: Boolean) {
        Log.v(TAG, "on next favorite $isFavorite")
        publisher.trySend(Event.Favorite(isFavorite))
    }

    private fun stopForeground() {
        if (!isForeground) {
            Log.w(TAG, "stop foreground request not not in foreground")
            return
        }
        Log.v(TAG, "stop foreground")

        service.stopForeground(true)
        notificationImpl.cancel()

        isForeground = false
    }

    private fun pauseForeground() {
        if (!isForeground) {
            Log.w(TAG, "pause foreground request not not in foreground")
            return
        }
        Log.v(TAG, "pause foreground")

        // state paused
        service.stopForeground(false)

        isForeground = false
    }

    private fun startForeground(notification: Notification) {
        if (isForeground) {
            Log.w(TAG, "start foreground request but already in foreground")
            return
        }
        Log.v(TAG, "start foreground")

        service.startForeground(INotification.NOTIFICATION_ID, notification)

        isForeground = true
    }

}
