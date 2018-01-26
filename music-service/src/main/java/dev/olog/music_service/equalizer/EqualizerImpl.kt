package dev.olog.music_service.equalizer

import android.media.audiofx.Equalizer
import dev.olog.domain.interactor.prefs.EqualizerPrefsUseCase
import dev.olog.shared_android.RootUtils
import dev.olog.shared_android.interfaces.equalizer.IEqualizer
import javax.inject.Inject

class EqualizerImpl @Inject constructor(
        private val equalizerPrefsUseCase: EqualizerPrefsUseCase

) : IEqualizer {

    private var equalizer = Equalizer(0, 1)
    private val listeners = mutableListOf<IEqualizer.Listener>()

    init {
        if (!RootUtils.isDeviceRooted()){
            val settings = equalizerPrefsUseCase.getEqualizerSettings()
            if (settings.isNotBlank()){
                equalizer.properties = Equalizer.Settings(settings)
            }
        } else {
            equalizer.release()
        }
    }

    override fun addListener(listener: IEqualizer.Listener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: IEqualizer.Listener) {
        listeners.remove(listener)
    }

    override fun getBandLevel(band: Int): Float = equalizer.getBandLevel(band.toShort()).toFloat()

    override fun setBandLevel(band: Int, level: Float) {
        equalizer.setBandLevel(band.toShort(), level.toShort())
    }

    override fun setPreset(position: Int) {
        equalizer.usePreset(position.toShort())
        listeners.forEach {
            for (band in 0 until equalizer.numberOfBands){
                val level = equalizer.getBandLevel(band.toShort()) / 100
                it.onPresetChange(band, level.toFloat())
            }
        }
    }

    override fun getPresets(): List<String> {
        return (0 until equalizer.numberOfPresets)
                .map { equalizer.getPresetName(it.toShort()) }
    }

    override fun getCurrentPreset(): Int = equalizer.currentPreset.toInt()

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        val settings = equalizer.properties
        equalizer.release()
        equalizer = Equalizer(0, audioSessionId)
        equalizer.enabled = equalizerPrefsUseCase.isEqualizerEnabled()
        settings?.let { equalizer.properties = it }
    }

    override fun setEnabled(enabled: Boolean) {
        equalizer.enabled = enabled
    }

    override fun release() {
        equalizerPrefsUseCase.saveEqualizerSettings(equalizer.properties.toString())
        equalizer.release()
    }
}