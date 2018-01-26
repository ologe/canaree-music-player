package dev.olog.music_service.equalizer

import android.media.audiofx.Virtualizer
import dev.olog.domain.interactor.prefs.EqualizerPrefsUseCase
import dev.olog.shared_android.interfaces.equalizer.IVirtualizer
import javax.inject.Inject

class VirtualizerImpl @Inject constructor(
        private val equalizerPrefsUseCase: EqualizerPrefsUseCase

) : IVirtualizer {

    private var virtualizer = Virtualizer(0, 1)

    override fun getStrength(): Int = virtualizer.roundedStrength.toInt()

    override fun setStrength(value: Int) {
        virtualizer.setStrength(value.toShort())
    }

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        val settings = virtualizer.properties
        virtualizer.release()
        virtualizer = Virtualizer(0, audioSessionId)
        virtualizer.enabled = equalizerPrefsUseCase.isEqualizerEnabled()
        settings?.let { virtualizer.properties = it }
    }

    override fun release() {
        equalizerPrefsUseCase.saveBassBoostSettings(virtualizer.properties.toString())
        virtualizer.release()
    }
}