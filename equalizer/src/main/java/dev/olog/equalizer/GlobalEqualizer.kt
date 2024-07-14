package dev.olog.equalizer

import dev.olog.core.entity.EqualizerBand
import dev.olog.core.entity.EqualizerPreset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlobalEqualizer @Inject constructor(
    private val manager: EqualizerManager,
) {

    fun setEnabled(enabled: Boolean) {
        for (data in manager.getAll()) {
            data.setEnabled(enabled)
        }
    }

    // equalizer
    fun getPresets(): List<EqualizerPreset> {
        return manager.getAll().first().equalizer?.getPresets() ?: return emptyList()
    }
    fun observeCurrentPreset(): Flow<EqualizerPreset> {
        return manager.getAll().first().equalizer?.observeCurrentPreset() ?: emptyFlow()
    }
    fun getCurrentPreset(): EqualizerPreset? {
        return manager.getAll().first().equalizer?.getCurrentPreset()
    }
    suspend fun setCurrentPreset(preset: EqualizerPreset) {
        for (data in manager.getAll()) {
            data.equalizer?.setCurrentPreset(preset)
        }
    }
    suspend fun updateCurrentPresetIfCustom() {
        for (data in manager.getAll()) {
            data.equalizer?.updateCurrentPresetIfCustom()
        }
    }

    fun getBandCount(): Int {
        return manager.getAll().first().equalizer?.getBandCount() ?: 0
    }
    fun getBandLevel(band: Int): Float {
        return manager.getAll().first().equalizer?.getBandLevel(band) ?: 0f
    }
    fun getAllBandsCurrentLevel(): List<EqualizerBand> {
        return manager.getAll().first().equalizer?.getAllBandsCurrentLevel() ?: emptyList()
    }
    fun setBandLevel(band: Int, level: Float) {
        for (data in manager.getAll()) {
            data.equalizer?.setBandLevel(band, level)
        }
    }
    fun getBandLimit(): Float {
        return manager.getAll().first().equalizer?.getBandLimit() ?: 0f
    }

    // bass boost
    fun getBassBoostStrength(): Int {
        return manager.getAll().first().bassBoost?.getStrength() ?: 0
    }
    fun setBassBoostStrength(value: Int) {
        for (data in manager.getAll()) {
            data.bassBoost?.setStrength(value)
        }
    }

    // virtualizer
    fun getVirtualizerStrength(): Int {
        return manager.getAll().first().virtualizer?.getStrength() ?: 0
    }
    fun setVirtualizerStrength(value: Int) {
        for (data in manager.getAll()) {
            data.virtualizer?.setStrength(value)
        }
    }

}