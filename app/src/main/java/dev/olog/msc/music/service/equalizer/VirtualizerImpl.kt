package dev.olog.msc.music.service.equalizer

import android.media.audiofx.Virtualizer
import dev.olog.msc.domain.interactor.prefs.EqualizerPrefsUseCase
import dev.olog.shared_android.interfaces.equalizer.IVirtualizer
import javax.inject.Inject

class VirtualizerImpl @Inject constructor(
        private val equalizerPrefsUseCase: EqualizerPrefsUseCase

) : IVirtualizer {

    private var virtualizer : Virtualizer? = null
    private var virtualizerSettings = Virtualizer.Settings()

    override fun getStrength(): Int {
        try {
            return virtualizer!!.roundedStrength.toInt()
        } catch (ex: Exception){
            return 0
        }
    }

    override fun setStrength(value: Int) {
        try {
            virtualizerSettings.strength = value.toShort()
            virtualizer!!.setStrength(value.toShort())
        } catch (ex: Exception){ }
    }

    override fun setEnabled(enabled: Boolean) {
        try {
            virtualizer!!.enabled = enabled
        } catch (ex: Exception){ }
    }

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        release()

        try {
            virtualizer = Virtualizer(0, audioSessionId)
            virtualizer!!.enabled = equalizerPrefsUseCase.isEqualizerEnabled()
            if (virtualizerSettings.toString().isNotBlank()){
                virtualizer!!.properties = virtualizerSettings
            }
        } catch (ex: Exception){ }
    }

    override fun release() {
        equalizerPrefsUseCase.saveVirtualizerSettings(virtualizerSettings.toString())
        try {
             virtualizer!!.release()
        } catch (ex: Exception){ }
    }



}