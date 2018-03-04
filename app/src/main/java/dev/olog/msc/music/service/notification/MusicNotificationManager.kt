package dev.olog.msc.music.service.notification

import android.app.Notification
import android.app.Service
import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.media.AudioManager
import android.support.v4.media.session.PlaybackStateCompat
import android.view.KeyEvent.KEYCODE_MEDIA_STOP
import dagger.Lazy
import dev.olog.msc.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.dagger.scope.PerService
import dev.olog.msc.music.service.interfaces.PlayerLifecycle
import dev.olog.msc.music.service.model.MediaEntity
import dev.olog.msc.utils.k.extension.dispatchEvent
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val SECONDS_TO_DESTROY = 120L // 2 min

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

    private val publisher = BehaviorSubject.create<Any>()
    private val currentState = MusicNotificationState()
    private val publishDisposable : Disposable? = null

    private val playerListener = object : PlayerLifecycle.Listener {
        override fun onPrepare(entity: MediaEntity) {
            onNextMetadata(entity)
        }

        override fun onMetadataChanged(entity: MediaEntity) {
            onNextMetadata(entity)
        }

        override fun onStateChanged(state: PlaybackStateCompat) {
            onNextState(state)
        }
    }

    init {
        lifecycle.addObserver(this)
        playerLifecycle.addListener(playerListener)

        notificationDisposable = publisher
                .toSerialized()
                .observeOn(Schedulers.computation())
                .filter {
                    when (it){
                        is MediaEntity -> currentState.isDifferentMetadata(it)
                        is PlaybackStateCompat -> currentState.isDifferentState(it)
                        else -> false
                    }
                }
                .subscribe({
                    publishDisposable.unsubscribe()
                    handler.removeCallbacks(runnable)
                    when (it){
                        is MediaEntity -> {
                            if (currentState.updateMetadata(it)) {
                                handler.postDelayed(runnable, 350)
                            }
                        }
                        is PlaybackStateCompat -> {
                            if (currentState.updateState(it)){
                                handler.postDelayed(runnable, 100)
                            }
                        }
                    }

                }, Throwable::printStackTrace)

    }

    private val runnable = Runnable {
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
        notificationDisposable.unsubscribe()
    }

    private fun onNextMetadata(metadata: MediaEntity) {
        publisher.onNext(metadata)
    }

    private fun onNextState(playbackState: PlaybackStateCompat) {
        publisher.onNext(playbackState)
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
                .timer(SECONDS_TO_DESTROY, TimeUnit.SECONDS)
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
