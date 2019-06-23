package dev.olog.msc.music.service

import android.content.Intent
import android.media.AudioManager
import android.view.KeyEvent
import android.view.KeyEvent.*
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dagger.Lazy
import dev.olog.msc.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.dagger.scope.PerService
import dev.olog.msc.utils.k.extension.dispatchEvent
import dev.olog.shared.unsubscribe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject


@PerService
class MediaButton @Inject internal constructor(
        @ServiceLifecycle lifecycle: Lifecycle,
        private val audioManager: Lazy<AudioManager>
//        private val speech: TrackSpeech

) : DefaultLifecycleObserver {

    private var disposable : Disposable? = null
    private var clicks = AtomicInteger(0)

    init {
        lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        disposable.unsubscribe()
    }

    fun onNextEvent(mediaButtonEvent: Intent) {
        val event = mediaButtonEvent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)

        if (event.action == ACTION_DOWN && event.keyCode == KEYCODE_HEADSETHOOK) {
            val current = clicks.incrementAndGet()

            if (current < 5){
                disposable.unsubscribe()
                disposable = Single.timer(300, TimeUnit.MILLISECONDS)
                        .map { current }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::dispatchEvent, Throwable::printStackTrace)
            }
        }
    }

    private fun dispatchEvent(clicks: Int) {
        when (clicks) {
            0 -> {}
            1 -> audioManager.get().dispatchEvent(KEYCODE_MEDIA_PLAY_PAUSE)
            2 -> audioManager.get().dispatchEvent(KEYCODE_MEDIA_NEXT)
            3 -> audioManager.get().dispatchEvent(KEYCODE_MEDIA_PREVIOUS)
//            else -> speech.speak()
        }
        this.clicks.set(0)
    }

}
