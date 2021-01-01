package dev.olog.service.music.player

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import dev.olog.domain.prefs.MusicPreferencesGateway
import dev.olog.service.music.interfaces.VolumeMaxValues
import dev.olog.shared.FlowInterval
import dev.olog.shared.autoDisposeJob
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.minutes

private const val VOLUME_DUCK = .2f
private const val VOLUME_LOWERED_DUCK = 0.1f

private const val VOLUME_NORMAL = 1f
private const val VOLUME_LOWERED_NORMAL = 0.4f

internal class PlayerVolume @Inject constructor(
    private val lifecycleOwner: LifecycleOwner,
    private val musicPrefs: MusicPreferencesGateway,
    private val calendar: Calendar,
) {

    private var updateVolumeJob by autoDisposeJob()

    fun updateVolume(to: Float) {
        if (updateVolumeJob?.isActive == true) {
            return
        }
        _volume.value = min(to, maxAllowedVolume())
    }

    private val _volume = MutableStateFlow(musicPrefs.volume.toFloat() / 100)
    private val _isDucking = MutableStateFlow(false)

    val volume: Flow<Float>
        get() = _volume

    private var volumeManager: VolumeMaxValues? = null

    init {
        val maxVolume = musicPrefs.observeVolume().map { it.toFloat() / 100 }

        // adjust max volume based on current time of day, check every 15 minutes
        musicPrefs.isMidnightMode()
            .combine(FlowInterval(15.minutes)) { isMidnightModeEnabled, _ -> getVolumeManager(isMidnightModeEnabled, isNight()) }
            .onEach { volumeManager = it }
            .combine(maxVolume) { manager, volume -> onVolumePreferencesChanged(manager, volume) }
            .launchIn(lifecycleOwner.lifecycleScope)
    }

    private fun onVolumePreferencesChanged(volumeValues: VolumeMaxValues, volume: Float) {
        val isDucking = _isDucking.value
        val maxVolume = volumeValues.maxVolume(isDucking)

        val newVolume = (volume * maxVolume).coerceAtMost(maxVolume)
        updateVolume(newVolume)
    }

    // from 10PM to 6AM
    // TODO test
    private fun isNight(): Boolean {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        return hour in 22..23 || hour in 0..6
    }

    private fun getVolumeManager(isMidnightModeEnabled: Boolean, isNight: Boolean): VolumeMaxValues {
        if (!isMidnightModeEnabled) {
            return Volume
        }

        if (isNight) {
            return NightVolume
        } else {
            return Volume
        }
    }

    fun setIsDucking(enabled: Boolean) {
        _isDucking.value = enabled

        if (enabled) {
            _volume.value = min(_volume.value, volumeManager?.duck ?: 1f)
        } else {
            _volume.value = min(_volume.value, volumeManager?.normal ?: 1f)
        }
    }

    // increase
    fun fadeIn(interval: Duration, min: Float, max: Float, delta: Float) {
        _volume.value = min

        updateVolumeJob = FlowInterval(interval)
            .takeWhile { _volume.value < max }
            .onEach {
                val newVolume = (_volume.value + delta).coerceIn(min, max)
                _volume.value = newVolume
            }
            .launchIn(lifecycleOwner.lifecycleScope)
    }

    // decrease
    fun fadeOut(interval: Duration, min: Float, max: Float, delta: Float) {

        updateVolumeJob = FlowInterval(interval)
            .takeWhile { _volume.value > min }
            .onEach {
                val newVolume = (_volume.value - delta).coerceIn(min, max)
                _volume.value = newVolume
            }
            .launchIn(lifecycleOwner.lifecycleScope)
    }

    fun stopFadeAndRestoreVolume() {
        updateVolumeJob = null
        restoreDefaultVolume()
    }

    fun maxAllowedVolume(): Float {
        val isDucking = _isDucking.value
        return (volumeManager?.maxVolume(isDucking) ?: 1f) * // max allowed volume
            (musicPrefs.volume.toFloat() / 100) // max settings volume
    }

    fun restoreDefaultVolume() {
        _volume.value = maxAllowedVolume()
    }

}

private object Volume : VolumeMaxValues {
    override val normal: Float = VOLUME_NORMAL
    override val duck: Float = VOLUME_DUCK
}

private object NightVolume : VolumeMaxValues {
    override val normal: Float = VOLUME_LOWERED_NORMAL
    override val duck: Float = VOLUME_LOWERED_DUCK
}
