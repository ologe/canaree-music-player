package dev.olog.service.music.notification

import android.app.Notification
import android.app.Service
import android.media.AudioManager
import android.support.v4.media.session.PlaybackStateCompat
import android.view.KeyEvent.KEYCODE_MEDIA_STOP
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dagger.Lazy
import dev.olog.injection.dagger.ServiceLifecycle
import dev.olog.injection.dagger.PerService
import dev.olog.core.entity.favorite.FavoriteEnum
import dev.olog.core.interactor.ObserveFavoriteAnimationUseCase
import dev.olog.service.music.interfaces.INotification
import dev.olog.service.music.model.MusicNotificationState
import dev.olog.shared.utils.isOreo
import dev.olog.shared.extensions.dispatchEvent
import dev.olog.shared.extensions.unsubscribe
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val MINUTES_TO_DESTROY = 30L

@PerService
class MusicNotificationManager @Inject constructor(
    private val service: Service,
    @ServiceLifecycle lifecycle: Lifecycle,
    private val audioManager: Lazy<AudioManager>,
    private val notificationImpl: INotification,
    observeFavoriteUseCase: ObserveFavoriteAnimationUseCase,
    playerLifecycle: dev.olog.service.music.interfaces.PlayerLifecycle

) : DefaultLifecycleObserver {

    private var isForeground: Boolean = false

    private var stopServiceAfterDelayDisposable: Disposable? = null
    private val subscriptions = CompositeDisposable()

    private val publisher = BehaviorSubject.create<Any>()
    private val currentState = MusicNotificationState()
    private var publishDisposable : Disposable? = null

    private val playerListener = object : dev.olog.service.music.interfaces.PlayerLifecycle.Listener {
        override fun onPrepare(entity: dev.olog.service.music.model.MediaEntity) {
            onNextMetadata(entity)
        }

        override fun onMetadataChanged(entity: dev.olog.service.music.model.MediaEntity) {
            onNextMetadata(entity)
        }

        override fun onStateChanged(state: PlaybackStateCompat) {
            onNextState(state)
        }
    }

    init {
        lifecycle.addObserver(this)
        playerLifecycle.addListener(playerListener)

        publisher.toSerialized()
                .observeOn(Schedulers.computation())
                .filter {
                    when (it){
                        is dev.olog.service.music.model.MediaEntity -> currentState.isDifferentMetadata(it)
                        is PlaybackStateCompat -> currentState.isDifferentState(it)
                        is Boolean -> currentState.isDifferentFavorite(it)
                        else -> false
                    }
                }
                .subscribe({
                    publishDisposable.unsubscribe()

                    when (it){
                        is dev.olog.service.music.model.MediaEntity -> {
                            if (currentState.updateMetadata(it)) {
                                publishNotification(350)
                            }
                        }
                        is PlaybackStateCompat -> {
                            val state = currentState.updateState(it)
                            if (state){
                                publishNotification(100)
                            }
                        }
                        is Boolean -> {
                            if (currentState.updateFavorite(it)){
                                publishNotification(100)
                            }
                        }
                    }

                }, Throwable::printStackTrace)
                .addTo(subscriptions)

        observeFavoriteUseCase.execute()
                .map { it == FavoriteEnum.FAVORITE || it == FavoriteEnum.ANIMATE_TO_FAVORITE }
                .distinctUntilChanged()
                .subscribe(this::onNextFavorite, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

    private fun publishNotification(delay: Long){
        if (!isForeground && isOreo()){
            // oreo needs to post notification immediately after calling startForegroundService
            issueNotification()
        } else {
            // post delayed
            publishDisposable = Single.timer(delay, TimeUnit.MILLISECONDS)
                    .subscribe({ issueNotification() }, Throwable::printStackTrace)
        }
    }

    private fun issueNotification() {
        val copy = currentState.copy()
        val notification = notificationImpl.update(copy)
        if (copy.isPlaying){
            startForeground(notification)
        } else {
            pauseForeground()
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        stopForeground()
        stopServiceAfterDelayDisposable.unsubscribe()
        publishDisposable.unsubscribe()
        subscriptions.clear()
    }

    private fun onNextMetadata(metadata: dev.olog.service.music.model.MediaEntity) {
        publisher.onNext(metadata)
    }

    private fun onNextState(playbackState: PlaybackStateCompat) {
        publisher.onNext(playbackState)
    }

    private fun onNextFavorite(isFavorite: Boolean){
        publisher.onNext(isFavorite)
    }

    private fun stopForeground() {
        if (!isForeground) {
            return
        }

        service.stopForeground(true)
        notificationImpl.cancel()

        isForeground = false
    }

    private fun pauseForeground() {
        if (!isForeground) {
            return
        }

        // state paused
        service.stopForeground(false)

        stopServiceAfterDelayDisposable.unsubscribe()

        stopServiceAfterDelayDisposable = Single
                .timer(MINUTES_TO_DESTROY, TimeUnit.MINUTES)
                .subscribe({ audioManager.get().dispatchEvent(KEYCODE_MEDIA_STOP) },
                        Throwable::printStackTrace)

        isForeground = false
    }

    private fun startForeground(notification: Notification) {
        if (isForeground) {
            return
        }

        service.startForeground(INotification.NOTIFICATION_ID, notification)

        // restart countdown
        stopServiceAfterDelayDisposable.unsubscribe()

        isForeground = true
    }

}
