package dev.olog.msc.music.service.equalizer

import android.media.audiofx.Equalizer
import dev.olog.msc.domain.interactor.prefs.EqualizerPrefsUseCase
import dev.olog.shared_android.interfaces.equalizer.IEqualizer
import javax.inject.Inject

class EqualizerImpl @Inject constructor(
        private val equalizerPrefsUseCase: EqualizerPrefsUseCase

) : IEqualizer {

    private var equalizer : Equalizer? = null
    private val listeners = mutableListOf<IEqualizer.Listener>()
    private var eqSettings = Equalizer.Settings()

    override fun addListener(listener: IEqualizer.Listener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: IEqualizer.Listener) {
        listeners.remove(listener)
    }

    override fun getBandLevel(band: Int): Float {
        try {
            return equalizer!!.getBandLevel(band.toShort()).toFloat()
        } catch (ex: Exception){
            return 0f
        }
    }

    override fun setBandLevel(band: Int, level: Float) {
        try {
            eqSettings.bandLevels[band] = level.toShort()
            equalizer!!.setBandLevel(band.toShort(), level.toShort())
        } catch (ex: Exception){ }
    }

    override fun setPreset(position: Int) {
        try {
            eqSettings.curPreset = position.toShort()

            equalizer!!.usePreset(position.toShort())

            listeners.forEach {
                for (band in 0 until equalizer!!.numberOfBands){
                    val level = equalizer!!.getBandLevel(band.toShort()) / 100
                    it.onPresetChange(band, level.toFloat())
                }
            }
        } catch (ex: Exception){ }
    }

    override fun getPresets(): List<String> {
        try {
            return (0 until equalizer!!.numberOfPresets)
                    .map { equalizer!!.getPresetName(it.toShort()) }
        } catch (ex: Exception){
            return listOf("")
        }
    }

    override fun getCurrentPreset(): Int {
        try {
            return equalizer!!.currentPreset.toInt()
        } catch (ex: Exception){
            return 0
        }
    }

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        release()

        try {
            equalizer = Equalizer(0, audioSessionId)
            equalizer!!.enabled = equalizerPrefsUseCase.isEqualizerEnabled()

            if (eqSettings.toString().isNotBlank()){
                equalizer!!.properties = eqSettings
            }
        } catch (ex: Exception){}

    }

    override fun setEnabled(enabled: Boolean) {
        try {
            equalizer!!.enabled = enabled
        } catch (ex: Exception){}

    }

    override fun release() {
        equalizerPrefsUseCase.saveEqualizerSettings(eqSettings.toString())
        try {
            equalizer!!.release()
        } catch (ex: Exception){}
    }

}