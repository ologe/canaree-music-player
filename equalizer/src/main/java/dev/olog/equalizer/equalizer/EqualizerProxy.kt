package dev.olog.equalizer.equalizer

import dev.olog.core.entity.EqualizerBand
import dev.olog.core.entity.EqualizerPreset
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class EqualizerProxy @Inject constructor(
    private val equalizer1: IEqualizerInternal,
    private val equalizer2: IEqualizerInternal
) : IEqualizer {

    private val cache = mutableMapOf<Int, IEqualizerInternal>()

    private fun retrieveImpl(callerHash: Int): IEqualizerInternal {
        if (!cache.containsKey(callerHash)) {
            when {
                // check if eq 1 is used, and if not assign it
                !cache.containsValue(equalizer1) -> cache[callerHash] = equalizer1
                // check if eq 2 is used, and if not assign it
                !cache.containsValue(equalizer2) -> cache[callerHash] = equalizer2
                else -> {
                    // if something goes wrong, fallback to eq 1
                    cache[callerHash] = equalizer1
                }
            }
        }
        return cache[callerHash]!!
    }

    override fun onAudioSessionIdChanged(callerHash: Int, audioSessionId: Int) {
        retrieveImpl(callerHash).onAudioSessionIdChanged(audioSessionId)
    }

    override fun onDestroy() {
        equalizer1.onDestroy()
        equalizer2.onDestroy()
    }

    override fun setEnabled(enabled: Boolean) {
        equalizer1.setEnabled(enabled)
        equalizer2.setEnabled(enabled)
    }

    override fun getPresets(): List<EqualizerPreset> {
        return equalizer1.getPresets()
    }

    override fun observeCurrentPreset(): Flow<EqualizerPreset> {
        return equalizer1.observeCurrentPreset()
    }

    override fun getCurrentPreset(): EqualizerPreset {
        return equalizer1.getCurrentPreset()
    }

    override suspend fun setCurrentPreset(preset: EqualizerPreset) {
        equalizer1.setCurrentPreset(preset)
        equalizer2.setCurrentPreset(preset)
    }

    override suspend fun updateCurrentPresetIfCustom() {
        equalizer1.updateCurrentPresetIfCustom()
        equalizer2.updateCurrentPresetIfCustom()
    }

    override fun getBandCount(): Int {
        return equalizer1.getBandCount()
    }

    override fun getBandLevel(band: Int): Float {
        return equalizer1.getBandLevel(band)
    }

    override fun getAllBandsCurrentLevel(): List<EqualizerBand> {
        return equalizer1.getAllBandsCurrentLevel()
    }

    override fun setBandLevel(band: Int, level: Float) {
        equalizer1.setBandLevel(band, level)
        equalizer2.setBandLevel(band, level)
    }

    override fun getBandLimit(): Float {
        return equalizer1.getBandLimit()
    }
}