package dev.olog.lib.equalizer.equalizer

import android.os.Build
import dagger.Lazy
import dev.olog.domain.entity.EqualizerBand
import dev.olog.domain.entity.EqualizerPreset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import timber.log.Timber
import javax.inject.Inject

internal class EqualizerWrapper @Inject constructor(
    private val equalizerImpl: Lazy<EqualizerImpl>,
    equalizerImpl28: Lazy<EqualizerImpl28>
) : IEqualizerInternal {

    private var currentEqualizer: IEqualizerInternal? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            equalizerImpl28.get()
        } else {
            equalizerImpl.get()
        }

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        try {
            currentEqualizer?.onAudioSessionIdChanged(audioSessionId)
        } catch (ex: Exception) {
            Timber.e(ex, "fallback to default equalizer")
            // sometimes equalizer impl 28 fails, fallback to default equalizer
            currentEqualizer = equalizerImpl.get()
            try {
                currentEqualizer?.onAudioSessionIdChanged(audioSessionId)
            } catch (ex: Exception) {
                Timber.e(ex, "fallback to null equalizer")
                // not sure why but is default equalizer, null it
                currentEqualizer = null
            }
        }
    }

    override fun onDestroy() {
        currentEqualizer?.onDestroy()
    }

    override fun setEnabled(enabled: Boolean) {
        currentEqualizer?.setEnabled(enabled)
    }

    override fun getPresets(): List<EqualizerPreset> {
        return currentEqualizer?.getPresets() ?: emptyList()
    }

    override fun observeCurrentPreset(): Flow<EqualizerPreset> {
        return currentEqualizer?.observeCurrentPreset() ?: emptyFlow()
    }

    override fun getCurrentPreset(): EqualizerPreset {
        return currentEqualizer?.getCurrentPreset()
            ?: EqualizerPreset(-1, "Default", emptyList(), true)
    }

    override suspend fun setCurrentPreset(preset: EqualizerPreset) {
        currentEqualizer?.setCurrentPreset(preset)
    }

    override suspend fun updateCurrentPresetIfCustom() {
        currentEqualizer?.updateCurrentPresetIfCustom()
    }

    override fun getBandCount(): Int {
        return currentEqualizer?.getBandCount() ?: 0
    }

    override fun getBandLevel(band: Int): Float {
        return currentEqualizer?.getBandLevel(band) ?: 0f
    }

    override fun getAllBandsCurrentLevel(): List<EqualizerBand> {
        return currentEqualizer?.getAllBandsCurrentLevel() ?: emptyList()
    }

    override fun setBandLevel(band: Int, level: Float) {
        currentEqualizer?.setBandLevel(band, level)
    }

    override fun getBandLimit(): Float {
        return currentEqualizer?.getBandLimit() ?: 0f
    }
}