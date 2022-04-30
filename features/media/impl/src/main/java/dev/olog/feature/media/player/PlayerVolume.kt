package dev.olog.feature.media.player

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.core.ServiceScope
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.platform.ServiceLifecycle
import dev.olog.feature.media.interfaces.IDuckVolume
import dev.olog.feature.media.interfaces.IMaxAllowedPlayerVolume
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val VOLUME_DUCK = .2f
private const val VOLUME_LOWERED_DUCK = 0.1f

private const val VOLUME_NORMAL = 1f
private const val VOLUME_LOWERED_NORMAL = 0.4f

@ServiceScoped
internal class PlayerVolume @Inject constructor(
    @ServiceLifecycle lifecycle: Lifecycle,
    musicPreferencesUseCase: MusicPreferencesGateway,
    serviceScope: ServiceScope,
) : IMaxAllowedPlayerVolume,
    DefaultLifecycleObserver {

    override var listener: IMaxAllowedPlayerVolume.Listener? = null

    private var volume: IDuckVolume = Volume()
    private var isDucking = false

    init {
        lifecycle.addObserver(this)

        // observe to preferences
        musicPreferencesUseCase.isMidnightMode()
            .onEach { lowerAtNight ->
                volume = if (!lowerAtNight) {
                    provideVolumeManager(false)
                } else {
                    provideVolumeManager(isNight())
                }

                listener?.onMaxAllowedVolumeChanged(getMaxAllowedVolume())
            }.launchIn(serviceScope)

        // observe at interval of 15 mins to detect if is day or night when
        // settigs is on
        musicPreferencesUseCase.isMidnightMode()
            .filter { it }
            .map { delay(TimeUnit.MINUTES.toMillis(15)); it; }
            .map { isNight() }
            .onEach { isNight ->
                volume = provideVolumeManager(isNight)
                listener?.onMaxAllowedVolumeChanged(getMaxAllowedVolume())
            }.launchIn(serviceScope)
    }

    private fun isNight(): Boolean {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return hour in 0..6
    }

    override fun getMaxAllowedVolume(): Float {
        return if (isDucking) volume.duck else volume.normal
    }

    private fun provideVolumeManager(isNight: Boolean): IDuckVolume {
        return if (isNight) {
            NightVolume()
        } else {
            Volume()
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
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

private class Volume : IDuckVolume {
    override val normal: Float = VOLUME_NORMAL
    override val duck: Float = VOLUME_DUCK
}

private class NightVolume : IDuckVolume {
    override val normal: Float = VOLUME_LOWERED_NORMAL
    override val duck: Float = VOLUME_LOWERED_DUCK
}
