package dev.olog.music_service.equalizer

import android.media.audiofx.Equalizer
import dev.olog.domain.interactor.prefs.EqualizerPrefsUseCase
import dev.olog.shared_android.interfaces.equalizer.IEqualizer
import javax.inject.Inject

class EqualizerImpl @Inject constructor(
        private val equalizerPrefsUseCase: EqualizerPrefsUseCase

) : IEqualizer {

    private var equalizer = Equalizer(0, 1)
    private val listeners = mutableListOf<IEqualizer.Listener>()

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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPresets(): List<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCurrentPreset(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun release() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}