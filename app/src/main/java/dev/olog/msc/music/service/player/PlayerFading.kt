package dev.olog.msc.music.service.player

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.support.v4.math.MathUtils
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.SimpleExoPlayer
import dev.olog.msc.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.dagger.scope.PerService
import dev.olog.msc.domain.interactor.prefs.MusicPreferencesUseCase
import dev.olog.msc.music.service.interfaces.ExoPlayerListenerWrapper
import dev.olog.msc.music.service.interfaces.PlayerLifecycle
import dev.olog.msc.music.service.model.MediaEntity
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@PerService
class PlayerFading @Inject constructor(
        @ServiceLifecycle lifecycle: Lifecycle,
        private val exoPlayer: SimpleExoPlayer,
        private val playerVolume: PlayerVolume,
        musicPreferencesUseCase: MusicPreferencesUseCase

) : DefaultLifecycleObserver, ExoPlayerListenerWrapper, PlayerLifecycle.Listener {

    init {
        lifecycle.addObserver(this)
        exoPlayer.addListener(this)
    }

    private var playerLifecycle: PlayerLifecycle? = null

    private var fadeTime = 0

    private var isFadingOut = false

    private var fadeInDisposable : Disposable? = null
    private var fadeOutDisposable : Disposable? = null
    private var isSongEnding: Disposable? = null

    private val crossFadeDurationDisposable = musicPreferencesUseCase
            .observeCrossFade(true)
            .subscribe({ fadeTime = it }, Throwable::printStackTrace)

    override fun onDestroy(owner: LifecycleOwner) {
        crossFadeDurationDisposable.unsubscribe()
        exoPlayer.removeListener(this)
        fadeInDisposable.unsubscribe()
        fadeOutDisposable.unsubscribe()
        isSongEnding.unsubscribe()
    }

    fun setPlayerLifecycle(playerLifecycle: PlayerLifecycle){
        this.playerLifecycle = playerLifecycle
        this.playerLifecycle?.addListener(this)
    }

    override fun onMetadataChanged(entity: MediaEntity) {
        fadeIn(false)
    }

    override fun onSeek(where: Long) {
        // need to go back to normal volume
        fadeIn(true)
    }

    /**
     * from low to normal
     */
    private fun fadeIn(fromCurrentVolume: Boolean){
        println("fade in request, duration $fadeTime")

        if (fadeTime == 0){
            exoPlayer.volume = playerVolume.getVolume()
            return
        }

        isFadingOut = false

        fadeOutDisposable.unsubscribe()
        fadeInDisposable.unsubscribe()

        val min = 0.05f
        val max = playerVolume.getVolume()
        val interval = 200L
        var times = fadeTime / interval

        if (fromCurrentVolume){
            times /= 4
        }

        val delta = Math.abs(max - min) / times

        if (!fromCurrentVolume){
            exoPlayer.volume = min
        }

        fadeInDisposable = Observable.interval(interval, TimeUnit.MILLISECONDS)
                .takeWhile { exoPlayer.volume < 1f }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val current = MathUtils.clamp(exoPlayer.volume + delta, min, max)
                    println("fading in, volume $current")
                    exoPlayer.volume = current
                }, Throwable::printStackTrace)

    }

    /**
     * from current to low
     */
    private fun fadeOut(fadeTime: Long = this.fadeTime.toLong()){
        println("fade out request, duration $fadeTime")

        if (fadeTime == 0L){
            exoPlayer.volume = playerVolume.getVolume()
            return
        }

        isFadingOut = true

        fadeInDisposable.unsubscribe()
        fadeOutDisposable.unsubscribe()

        val min = 0f
        val max = exoPlayer.volume
        val interval = 200L
        val times = fadeTime / interval
        val delta = (Math.abs(max - min) / times) * 1.02f

        fadeOutDisposable = Observable.interval(interval, TimeUnit.MILLISECONDS)
                .takeWhile { exoPlayer.volume > min }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val current = MathUtils.clamp(exoPlayer.volume - delta, min, max)
                    println("fading out, volume $current")
                    exoPlayer.volume = current

                }, { isFadingOut = false }, { isFadingOut = false })
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        isSongEnding.unsubscribe()

        if (playWhenReady){
            isSongEnding = Observable.interval(1, TimeUnit.SECONDS)
                    .filter { !isFadingOut }
                    .filter { (exoPlayer.duration - exoPlayer.currentPosition) < fadeTime }
                    .map { exoPlayer.duration - exoPlayer.currentPosition }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ fadeOut(it) }, Throwable::printStackTrace)
        }
    }

    override fun onPlayerError(error: ExoPlaybackException?) {
    }
}