package dev.olog.music_service.equalizer

import android.media.audiofx.Virtualizer
import dev.olog.domain.interactor.prefs.EqualizerPrefsUseCase
import dev.olog.shared_android.RootUtils
import dev.olog.shared_android.interfaces.equalizer.IVirtualizer
import javax.inject.Inject

class VirtualizerImpl @Inject constructor(
        private val equalizerPrefsUseCase: EqualizerPrefsUseCase

) : SafeAudioFx(), IVirtualizer {

    private var virtualizer = Virtualizer(0, 1)
    private var virtualizerSettings = Virtualizer.Settings()

    init {
        if (!RootUtils.isDeviceRooted()){
            val settings = equalizerPrefsUseCase.getVirtualizerSettings()
            if (settings.isNotBlank()){
                virtualizerSettings = Virtualizer.Settings(settings)
            } else {
                virtualizerSettings = virtualizer.properties
            }
        } else {
            virtualizer.release()
            isReleased = true
        }
    }

    override fun getStrength(): Int {
        return try {
            virtualizer.roundedStrength.toInt()
        } catch (ex: Exception){ 0 }
    }

    override fun setStrength(value: Int) {
        safeEdit {
            virtualizerSettings.strength = value.toShort()
            virtualizer.setStrength(value.toShort())
        }
    }

    override fun setEnabled(enabled: Boolean) {
        safeEdit {
            virtualizer.enabled = enabled
        }
    }

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        release()

        virtualizer = Virtualizer(0, audioSessionId)
        isReleased = false
        virtualizer.enabled = equalizerPrefsUseCase.isEqualizerEnabled()
        if (virtualizerSettings.toString().isNotBlank()){
            virtualizer.properties = virtualizerSettings
        }
    }

    override fun release() {
        equalizerPrefsUseCase.saveVirtualizerSettings(virtualizerSettings.toString())
        super.release(virtualizer)
    }



}