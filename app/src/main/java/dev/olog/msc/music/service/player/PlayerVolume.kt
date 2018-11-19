package dev.olog.msc.music.service.player

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.msc.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.dagger.scope.PerService
import dev.olog.msc.domain.gateway.prefs.MusicPreferencesGateway
import dev.olog.msc.music.service.volume.IPlayerVolume
import dev.olog.msc.music.service.volume.IVolume
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val VOLUME_DUCK = .2f
private const val VOLUME_LOWERED_DUCK = 0.1f

private const val VOLUME_NORMAL = 1f
private const val VOLUME_LOWERED_NORMAL = 0.4f

@PerService
class PlayerVolume @Inject constructor(
        @ServiceLifecycle lifecycle: Lifecycle,
        musicPreferencesUseCase: MusicPreferencesGateway

) : IPlayerVolume, DefaultLifecycleObserver {

    override var listener: IPlayerVolume.Listener? = null
    private var disposable: Disposable? = null
    private var intervalDisposable: Disposable? = null

    private var volume: IVolume = Volume()
    private var isDucking = false

    init {
        lifecycle.addObserver(this)

        // observe to preferences
        disposable = musicPreferencesUseCase.isMidnightMode()
                .subscribe({ lowerAtNight ->
                    if (!lowerAtNight) {
                        volume = provideVolumeManager(false)
                    } else {
                        volume = provideVolumeManager(isNight())
                    }

                    listener?.onVolumeChanged(getVolume())
                }, Throwable::printStackTrace)

        // observe at interval of 15 mins to detect if is day or night when
        // settigs is on
        intervalDisposable = musicPreferencesUseCase.isMidnightMode()
                .filter { it }
                .flatMap { Observable.interval(15, TimeUnit.MINUTES) }
                .observeOn(AndroidSchedulers.mainThread())
                .map { isNight() }
                .subscribe({ isNight ->
                    volume = provideVolumeManager(isNight)
                    listener?.onVolumeChanged(getVolume())

                }, Throwable::printStackTrace)
    }

    private fun isNight(): Boolean {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return hour <= 6 || hour >= 21
    }

    override fun getVolume(): Float {
        return if (isDucking) volume.duck else volume.normal
    }

    private fun provideVolumeManager(isNight: Boolean): IVolume {
        return if (isNight) {
            NightVolume()
        } else {
            Volume()
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        disposable.unsubscribe()
        intervalDisposable.unsubscribe()
        listener = null
    }

    override fun normal(): Float {
        isDucking = false
        return volume.normal
    }

    override fun ducked(): Float {
        isDucking = true
        return volume.duck
    }
}

private class Volume : IVolume {
    override val normal: Float = VOLUME_NORMAL
    override val duck: Float = VOLUME_DUCK
}

private class NightVolume : IVolume {
    override val normal: Float = VOLUME_LOWERED_NORMAL
    override val duck: Float = VOLUME_LOWERED_DUCK
}
