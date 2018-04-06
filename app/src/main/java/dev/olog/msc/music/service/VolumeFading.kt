package dev.olog.msc.music.service

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.support.v4.math.MathUtils
import com.google.android.exoplayer2.SimpleExoPlayer
import dev.olog.msc.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.dagger.scope.PerService
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val MIN = .2f
private const val MAX = 1f
private const val DELTA = .05f

@PerService
class VolumeFading @Inject constructor(
        @ServiceLifecycle lifecycle: Lifecycle,
        private val exoPlayer: SimpleExoPlayer

) : DefaultLifecycleObserver {

    private var disposable: Disposable? = null

    init {
        lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        disposable.unsubscribe()
    }

    /**
     * from low to normal
     */
    fun fadeIn(fromVolume: Float? = null){
        fromVolume?.let { exoPlayer.volume = it }

        disposable.unsubscribe()
        disposable = Observable.interval(35, TimeUnit.MILLISECONDS)
                .takeWhile { exoPlayer.volume < 1f }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    var current = exoPlayer.volume
                    current = MathUtils.clamp(current + DELTA, MIN, MAX)
                    exoPlayer.volume = current
                })
    }

    /**
     * from normal to low
     */
    fun fadeOut(){
        exoPlayer.volume = .8f

        disposable.unsubscribe()
        disposable = Observable.interval(35, TimeUnit.MILLISECONDS)
                .takeWhile { exoPlayer.volume > MIN }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    var current = exoPlayer.volume
                    current = MathUtils.clamp(current - DELTA, MIN, MAX)
                    exoPlayer.volume = current
                })
    }

}