package dev.olog.music_service

import android.app.Notification
import android.app.Service
import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.media.AudioManager
import android.support.v4.media.session.PlaybackStateCompat
import android.view.KeyEvent.KEYCODE_MEDIA_STOP
import dagger.Lazy
import dev.olog.music_service.di.PerService
import dev.olog.music_service.di.ServiceLifecycle
import dev.olog.music_service.interfaces.INotification
import dev.olog.music_service.interfaces.PlayerLifecycle
import dev.olog.music_service.model.MediaEntity
import dev.olog.music_service.utils.dispatchEvent
import dev.olog.shared.unsubscribe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val METADATA_DEBOUNCE = 250L
private const val NOTIFICATION_DEBOUNCE = 150L
private const val SECONDS_TO_DESTROY = 30

@PerService
class MusicNotificationManager @Inject constructor(
        private val service: Service,
        @ServiceLifecycle lifecycle: Lifecycle,
        private val audioManager: Lazy<AudioManager>,
        private val notificationImpl: INotification,
        playerLifecycle: PlayerLifecycle

) : DefaultLifecycleObserver {

    private var isForeground: Boolean = false

    private var stopServiceAfterDelayDisposable: Disposable? = null
    private var notificationDisposable: Disposable? = null

    private val metadataPublisher = BehaviorSubject.create<MediaEntity>()
    private val statePublisher = BehaviorSubject.create<PlaybackStateCompat>()

    private val playerListener = object : PlayerLifecycle.Listener {
        override fun onPrepare(entity: MediaEntity) {
            onNextMetadata(entity)
        }

        override fun onPlay(entity: MediaEntity) {
            onNextMetadata(entity)
        }

        override fun onStateChanged(state: PlaybackStateCompat) {
            onNextState(state)
        }
    }

    init {
        lifecycle.addObserver(this)
        playerLifecycle.addListener(playerListener)

        val metadataObservable = metadataPublisher
                .observeOn(Schedulers.computation())
                .debounce(METADATA_DEBOUNCE, TimeUnit.MILLISECONDS)

        val playbackStateObservable = statePublisher
                .observeOn(Schedulers.computation())
                .filter { playbackState ->
                    val state = playbackState.state
                    state == PlaybackStateCompat.STATE_PLAYING || state == PlaybackStateCompat.STATE_PAUSED
                }.distinctUntilChanged()

        notificationDisposable = Observables.combineLatest (
                metadataObservable,
                playbackStateObservable
        ) { metadata, playbackState -> update(playbackState, metadata) }
                .debounce(NOTIFICATION_DEBOUNCE, TimeUnit.MILLISECONDS)
                .map { notificationImpl.update() to it }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ pair ->
                    val state = pair.second
                    val notification = pair.first
                    if (state == PlaybackStateCompat.STATE_PLAYING) {
                        startForeground(notification)
                    } else if (state == PlaybackStateCompat.STATE_PAUSED) {
                        pauseForeground()
                    }
                })

    }

    override fun onDestroy(owner: LifecycleOwner) {
        stopForeground()
        stopServiceAfterDelayDisposable.unsubscribe()
        notificationDisposable.unsubscribe()
    }

    private fun onNextMetadata(metadata: MediaEntity) {
        metadataPublisher.onNext(metadata)
    }

    private fun onNextState(playbackState: PlaybackStateCompat) {
        statePublisher.onNext(playbackState)
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
                .timer(SECONDS_TO_DESTROY.toLong(), TimeUnit.SECONDS)
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

    private fun update(playbackState: PlaybackStateCompat,
                       metadata: MediaEntity): Int {

        notificationImpl.createIfNeeded()
        notificationImpl.updateState(playbackState)
        notificationImpl.updateMetadata(metadata)

        return playbackState.state
    }

}
