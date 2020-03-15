package dev.olog.service.music.notification

import android.app.Notification
import android.app.Service
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dev.olog.core.entity.favorite.FavoriteState
import dev.olog.core.interactor.favorite.ObserveFavoriteAnimationUseCase
import dev.olog.core.schedulers.Schedulers
import dev.olog.injection.dagger.PerService
import dev.olog.service.music.interfaces.INotification
import dev.olog.service.music.interfaces.IPlayerLifecycle
import dev.olog.service.music.model.Event
import dev.olog.service.music.model.MediaEntity
import dev.olog.service.music.model.MetadataEntity
import dev.olog.service.music.model.MusicNotificationState
import dev.olog.shared.CustomScope
import dev.olog.shared.android.utils.isOreo
import dev.olog.shared.autoDisposeJob
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@PerService
internal class MusicNotificationManager @Inject constructor(
    private val service: Service,
    private val notificationImpl: INotification,
    observeFavoriteUseCase: ObserveFavoriteAnimationUseCase,
    playerLifecycle: IPlayerLifecycle,
    schedulers: Schedulers

) : DefaultLifecycleObserver, CoroutineScope by CustomScope(schedulers.cpu) {

    companion object {
        @JvmStatic
        private val TAG = "SM:${MusicNotificationManager::class.java.simpleName}"
    }

    private var isForeground: Boolean = false

    private val publisher = Channel<Event>(Channel.UNLIMITED)
    private val currentState = MusicNotificationState()
    private var publishJob by autoDisposeJob()

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

    private fun onNextMetadata(metadata: MediaEntity) {
        Timber.v("$TAG on next metadata=${metadata.title}")
        publisher.offer(Event.Metadata(metadata))
    }

    private fun onNextState(playbackState: PlaybackStateCompat) {
        Timber.v("$TAG on next state")
        publisher.offer(Event.State(playbackState))
    }

    private fun onNextFavorite(isFavorite: Boolean) {
        Timber.v("$TAG on next favorite $isFavorite")
        publisher.offer(Event.Favorite(isFavorite))
    }

    init {
        playerLifecycle.addListener(playerListener)

        publisher.consumeAsFlow()
            .filter { event ->
                when (event) {
                    is Event.Metadata -> currentState.isDifferentMetadata(event.entity)
                    is Event.State -> currentState.isDifferentState(event.state)
                    is Event.Favorite -> currentState.isDifferentFavorite(event.favorite)
                }
            }.onEach { consumeEvent(it) }
            .launchIn(this)

        observeFavoriteUseCase()
            .map { it == FavoriteState.FAVORITE }
            .onEach { onNextFavorite(it) }
            .launchIn(this)
    }

    private suspend fun consumeEvent(event: Event){
        Timber.v("$TAG on next event $event")

        when (event){
            is Event.Metadata -> {
                if (currentState.updateMetadata(event.entity)) {
                    publishNotification(currentState.copy())
                }
            }
            is Event.State -> {
                if (currentState.updateState(event.state)){
                    publishNotification(currentState.copy())
                }
            }
            is Event.Favorite -> {
                if (currentState.updateFavorite(event.favorite)){
                    publishNotification(currentState.copy())
                }
            }
        }
    }

    private suspend fun publishNotification(state: MusicNotificationState) {
        require(currentState !== state) // to avoid concurrency problems a copy is passed

        Timber.v("$TAG publish notification state=$state")
        issueNotification(state)
    }

    private suspend fun issueNotification(state: MusicNotificationState) {
        Timber.v("$TAG issue notification")
        val notification = notificationImpl.update(state)
        if (state.isPlaying) {
            startForeground(notification)
        } else {
            pauseForeground()
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        stopForeground()
        publishJob = null
        publisher.close()
        cancel()
    }

    private fun stopForeground() {
        if (!isForeground) {
            Timber.w("$TAG stop foreground request not not in foreground")
            return
        }
        Timber.v("$TAG stop foreground")

        service.stopForeground(true)
        notificationImpl.cancel()

        isForeground = false
    }

    private fun pauseForeground() {
        if (!isForeground) {
            Timber.w("$TAG pause foreground request not not in foreground")
            return
        }
        Timber.v("$TAG pause foreground")

        // state paused
        service.stopForeground(false)

        isForeground = false
    }

    private fun startForeground(notification: Notification) {
        if (isForeground) {
            Timber.w("$TAG start foreground request but already in foreground")
            return
        }
        Timber.v("$TAG start foreground")

        service.startForeground(INotification.NOTIFICATION_ID, notification)

        isForeground = true
    }

}
