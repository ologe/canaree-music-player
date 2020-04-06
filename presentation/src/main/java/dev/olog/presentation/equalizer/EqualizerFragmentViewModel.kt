package dev.olog.presentation.equalizer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.domain.entity.EqualizerPreset
import dev.olog.domain.gateway.EqualizerGateway
import dev.olog.domain.prefs.EqualizerPreferencesGateway
import dev.olog.domain.schedulers.Schedulers
import dev.olog.equalizer.bassboost.IBassBoost
import dev.olog.equalizer.equalizer.IEqualizer
import dev.olog.equalizer.virtualizer.IVirtualizer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class EqualizerFragmentViewModel @Inject constructor(
    private val equalizer: IEqualizer,
    private val bassBoost: IBassBoost,
    private val virtualizer: IVirtualizer,
    private val equalizerPrefsUseCase: EqualizerPreferencesGateway,
    private val equalizerGateway: EqualizerGateway,
    private val schedulers: Schedulers
) : ViewModel() {

    val currentPreset : Flow<EqualizerPreset> = equalizer.observeCurrentPreset()
        .flowOn(schedulers.io)

    fun getBandLimit() = equalizer.getBandLimit()
    fun getBandCount() = equalizer.getBandCount()
    fun setCurrentPreset(preset: EqualizerPreset) = viewModelScope.launch(schedulers.io) {
        equalizer.setCurrentPreset(preset)
    }
    fun getPresets() = equalizer.getPresets()
    fun setBandLevel(band: Int, level: Float) = equalizer.setBandLevel(band, level)

    fun isEqualizerEnabled(): Boolean = equalizerPrefsUseCase.isEqualizerEnabled()

    fun setEqualizerEnabled(enabled: Boolean) {
        equalizer.setEnabled(enabled)
        virtualizer.setEnabled(enabled)
        bassBoost.setEnabled(enabled)
        equalizerPrefsUseCase.setEqualizerEnabled(enabled)
    }

    fun getBassStrength(): Int = bassBoost.getStrength()

    fun setBassStrength(value: Int) {
        bassBoost.setStrength(value)
    }

    fun getVirtualizerStrength(): Int = virtualizer.getStrength()

    fun setVirtualizerStrength(value: Int) {
        virtualizer.setStrength(value)
    }

    fun getBandStep(): Float {
        return .1f
    }

    fun deleteCurrentPreset() = viewModelScope.launch(schedulers.io) {
        val currentPreset = currentPreset.first()
        equalizerPrefsUseCase.setCurrentPresetId(0)
        equalizerGateway.deletePreset(currentPreset)
    }

    suspend fun addPreset(title: String): Boolean = withContext(schedulers.io){
        val preset = EqualizerPreset(
            id = -1,
            name = title,
            isCustom = true,
            bands = equalizer.getAllBandsCurrentLevel()
        )
        require(preset.bands.size == getBandCount()) {
            "current=${preset.bands.size}, requested=${getBandCount()}"
        }
        equalizerGateway.addPreset(preset)
        true
    }

    fun updateCurrentPresetIfCustom() = viewModelScope.launch(schedulers.io) {
        equalizer.updateCurrentPresetIfCustom()
    }

}
