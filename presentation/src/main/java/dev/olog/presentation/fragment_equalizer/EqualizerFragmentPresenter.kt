package dev.olog.presentation.fragment_equalizer

import dev.olog.domain.interactor.prefs.EqualizerPrefsUseCase
import dev.olog.shared_android.interfaces.equalizer.IBassBoost
import dev.olog.shared_android.interfaces.equalizer.IEqualizer
import dev.olog.shared_android.interfaces.equalizer.IReplayGain
import dev.olog.shared_android.interfaces.equalizer.IVirtualizer
import javax.inject.Inject

class EqualizerFragmentPresenter @Inject constructor(
        private val equalizer: IEqualizer,
        private val bassBoost: IBassBoost,
        private val virtualizer: IVirtualizer,
        private val replayGain: IReplayGain,
        private val equalizerPrefsUseCase: EqualizerPrefsUseCase
) {

    fun getPresets() = equalizer.getPresets()

    fun getCurrentPreset() = equalizer.getCurrentPreset()

    fun setPreset(position: Int){
        equalizer.setPreset(position)
    }

    fun isEqualizerEnabled(): Boolean = equalizerPrefsUseCase.isEqualizerEnabled()

    fun setEqualizerEnabled(enabled: Boolean){
        equalizer.setEnabled(enabled)
        virtualizer.setEnabled(enabled)
        bassBoost.setEnabled(enabled)
        equalizerPrefsUseCase.setEqualizerEnabled(enabled)
    }

    fun getBandLevel(band: Int): Float = equalizer.getBandLevel(band) / 100

    fun setBandLevel(band: Int, level: Float){
        equalizer.setBandLevel(band, level * 100)
    }

    fun isReplayGainEnabled(): Boolean {
        return replayGain.isImplementedByDevice() && equalizerPrefsUseCase.isReplayGainEnabled()
    }

    fun setReplayGainEnabled(enabled: Boolean) {
        replayGain.setEnabled(enabled)
        equalizerPrefsUseCase.setReplayGainEnabled(enabled)
    }

    fun getBassStrength(): Int = bassBoost.getStrength() / 10

    fun setBassStrength(value: Int){
        bassBoost.setStrength(value * 10)
    }

    fun getVirtualizerStrength(): Int = virtualizer.getStrength() / 10

    fun setVirtualizerStrength(value: Int){
        virtualizer.setStrength(value * 10)
    }

    fun addEqualizerListener(listener: IEqualizer.Listener){
        equalizer.addListener(listener)
    }

    fun removeEqualizerListener(listener: IEqualizer.Listener){
        equalizer.removeListener(listener)
    }

}
