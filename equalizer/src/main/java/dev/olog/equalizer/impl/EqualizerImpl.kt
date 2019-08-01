package dev.olog.equalizer.impl

import android.media.audiofx.Equalizer
import dev.olog.core.entity.EqualizerBand
import dev.olog.core.entity.EqualizerPreset
import dev.olog.core.gateway.EqualizerGateway
import dev.olog.core.prefs.EqualizerPreferencesGateway
import dev.olog.equalizer.IEqualizer
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class EqualizerImpl @Inject constructor(
    private val gateway: EqualizerGateway,
    private val prefs: EqualizerPreferencesGateway

) : IEqualizer {

    companion object {
        private const val BANDS = 5
    }

    private var equalizer: Equalizer? = null

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        onDestroy()

//        equalizer = Equalizer(0, audioSessionId).apply {
//            enabled = prefs.isEqualizerEnabled()
//            val lastProperties = prefs.getEqualizerSettings()
//            if (lastProperties.isNotBlank()) {
//                properties = Equalizer.Settings(lastProperties)
//            }
//        }
    }

    override fun getBandLevel(band: Int): Float {
        return equalizer?.getBandLevel(band.toShort())?.toFloat() ?: 0f
    }

    override fun setBandLevel(band: Int, level: Float) {
        equalizer?.setBandLevel(band.toShort(), level.toShort())?.also {
            save()
        }
    }

    override suspend fun setCurrentPreset(preset: EqualizerPreset) {
//        equalizer?.usePreset(position.toShort())
    }

    override fun getPresets(): List<EqualizerPreset> {
        TODO()
//        return try {
//            (0 until equalizer!!.numberOfPresets)
//                .map { equalizer!!.getPresetName(it.toShort()) }
//        } catch (ex: Throwable) {
//            return emptyList()
//        }
    }

    override suspend fun updateCurrentPresetIfCustom() {

    }

    override fun observeCurrentPreset(): Flow<EqualizerPreset> {
        TODO()
    }

    override fun getCurrentPreset(): EqualizerPreset {
        TODO()
//        return equalizer?.currentPreset?.toInt() ?: 0
    }

    override fun getBandCount(): Int = BANDS

    override fun setEnabled(enabled: Boolean) {
        equalizer?.enabled = enabled
    }

    override fun onDestroy() {
        equalizer?.release()
    }

    private fun save() {
//        val currentProperties = equalizer?.properties?.toString()
//        if (!currentProperties.isNullOrBlank()) {
//            prefs.saveEqualizerSettings(currentProperties)
//        }
    }

    override fun getBandLimit(): Float {
        TODO()
    }

    override fun getAllBandsLevel(): List<EqualizerBand> {
        TODO()
    }
}