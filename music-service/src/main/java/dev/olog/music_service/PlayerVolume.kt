package dev.olog.music_service

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import dev.olog.domain.interactor.prefs.GetLowerVolumeOnNightUseCase
import dev.olog.music_service.di.ServiceLifecycle
import dev.olog.shared.unsubscribe
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val VOLUME_DUCK = .2f
private const val VOLUME_LOWERED_DUCK = 0.1f

private const val VOLUME_NORMAL = 1f
private const val VOLUME_LOWERED_NORMAL = 0.4f

class PlayerVolume @Inject constructor(
        @ServiceLifecycle lifecycle: Lifecycle,
        lowerVolumeOnNightUseCase: GetLowerVolumeOnNightUseCase

) : DefaultLifecycleObserver {

    var listener: Listener? = null
    private var disposable: Disposable? = null
    private var intervalDisposable: Disposable? = null

    private var volume: IVolume = Volume()
    private var isDucking = false

    init {
        lifecycle.addObserver(this)

        disposable = lowerVolumeOnNightUseCase.observe()
                .subscribe({ lowerAtNight ->
                    if (!lowerAtNight){
                        volume = provideVolumeManager(false)
                    } else {
                        volume = provideVolumeManager(isNight())
                    }

                    listener?.onVolumeChanged(getVolume())
                }, Throwable::printStackTrace)

        intervalDisposable = lowerVolumeOnNightUseCase.observe()
                .filter { it }
                .flatMap { Flowable.interval(15, TimeUnit.MINUTES) }
                .observeOn(AndroidSchedulers.mainThread())
                .map { isNight() }
                .subscribe({ isNight ->
                    volume = provideVolumeManager(isNight)
                    listener?.onVolumeChanged(getVolume())

                }, Throwable::printStackTrace)
    }

    private fun isNight(): Boolean{
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return hour <= 6 || hour >= 21
    }

    private fun getVolume(): Float{
        return if (isDucking) volume.duck else volume.normal
    }

    private fun provideVolumeManager(isNight: Boolean): IVolume{
        return if (isNight){ NightVolume() } else { Volume() }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        disposable.unsubscribe()
        intervalDisposable.unsubscribe()
        listener = null
    }

    fun getNormalVolume(): Float{
        isDucking = false
        return volume.normal
    }

    fun getDuckingVolume(): Float {
        isDucking = true
        return volume.duck
    }

    @FunctionalInterface
    interface Listener {
        fun onVolumeChanged(volume: Float)
    }

}

private interface IVolume {
    val normal: Float
    val duck: Float
}

private class Volume : IVolume {
    override val normal: Float = VOLUME_NORMAL
    override val duck: Float = VOLUME_DUCK
}

private class NightVolume : IVolume {
    override val normal: Float = VOLUME_LOWERED_NORMAL
    override val duck: Float = VOLUME_LOWERED_DUCK
}
