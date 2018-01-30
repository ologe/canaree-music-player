package dev.olog.music_service.equalizer

import android.media.audiofx.Equalizer
import dev.olog.domain.interactor.prefs.EqualizerPrefsUseCase
import dev.olog.shared_android.BuildConfig
import dev.olog.shared_android.RootUtils
import dev.olog.shared_android.interfaces.equalizer.IEqualizer
import javax.inject.Inject

class EqualizerImpl @Inject constructor(
        private val equalizerPrefsUseCase: EqualizerPrefsUseCase

) : SafeAudioFx(), IEqualizer {

    private var equalizer = Equalizer(0, 1)
    private val listeners = mutableListOf<IEqualizer.Listener>()
    private var eqSettings = Equalizer.Settings()

    init {
        if (!RootUtils.isDeviceRooted()){
            val settings = equalizerPrefsUseCase.getEqualizerSettings()
            if (settings.isNotBlank()){
                eqSettings = Equalizer.Settings(settings)
            } else {
                eqSettings = equalizer.properties
            }
        } else {
            isReleased = true
            equalizer.release()
        }
    }

    override fun addListener(listener: IEqualizer.Listener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: IEqualizer.Listener) {
        listeners.remove(listener)
    }

    override fun getBandLevel(band: Int): Float {
        return try {
            equalizer.getBandLevel(band.toShort()).toFloat()
        } catch (ex: Exception){ 0f }
    }

    override fun setBandLevel(band: Int, level: Float) {
        safeEdit {
            eqSettings.bandLevels[band] = level.toShort()
            equalizer.setBandLevel(band.toShort(), level.toShort())
        }
    }

    override fun setPreset(position: Int) {
        safeEdit {
            eqSettings.curPreset = position.toShort()

            equalizer.usePreset(position.toShort())

            listeners.forEach {
                for (band in 0 until equalizer.numberOfBands){
                    val level = equalizer.getBandLevel(band.toShort()) / 100
                    it.onPresetChange(band, level.toFloat())
                }
            }
        }
    }

    override fun getPresets(): List<String> {
        return try {
            (0 until equalizer.numberOfPresets)
                    .map { equalizer.getPresetName(it.toShort()) }
        } catch (ex: Exception){
            if (BuildConfig.DEBUG) {
                ex.printStackTrace()
            }
            listOf("")
        }
    }

    override fun getCurrentPreset(): Int {
        return try {
            equalizer.currentPreset.toInt()
        } catch (ex: Exception){ 0 }
    }

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        release()

        equalizer = Equalizer(0, audioSessionId)
        isReleased = false
        equalizer.enabled = equalizerPrefsUseCase.isEqualizerEnabled()

        if (eqSettings.toString().isNotBlank()){
            equalizer.properties = eqSettings
        }
    }

    override fun setEnabled(enabled: Boolean) {
        safeEdit {
            equalizer.enabled = enabled
        }
    }

    private fun updateSettings(settings: Equalizer.Settings){

    }

    override fun release() {
        equalizerPrefsUseCase.saveEqualizerSettings(eqSettings.toString())
        super.release(equalizer)
    }

}