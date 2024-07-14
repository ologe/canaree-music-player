package dev.olog.presentation.equalizer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.entity.EqualizerPreset
import dev.olog.core.gateway.EqualizerGateway
import dev.olog.core.prefs.EqualizerPreferencesGateway
import dev.olog.equalizer.GlobalEqualizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
internal class EqualizerFragmentViewModel @Inject constructor(
    private val equalizer: GlobalEqualizer,
    private val equalizerPrefsUseCase: EqualizerPreferencesGateway,
    private val equalizerGateway: EqualizerGateway
) : ViewModel() {

    private val currentPresetLiveData = MutableLiveData<EqualizerPreset>()

    init {
        viewModelScope.launch {
            equalizer.observeCurrentPreset()
                .flowOn(Dispatchers.IO)
                .collect { currentPresetLiveData.value = it }
        }
    }

    fun getBandLimit() = equalizer.getBandLimit()
    fun getBandCount() = equalizer.getBandCount()
    fun setCurrentPreset(preset: EqualizerPreset) = viewModelScope.launch(Dispatchers.IO) {
        equalizer.setCurrentPreset(preset)
    }
    fun getPresets() = equalizer.getPresets()
    fun setBandLevel(band: Int, level: Float) = equalizer.setBandLevel(band, level)

    fun observePreset(): LiveData<EqualizerPreset> = currentPresetLiveData

    fun isEqualizerEnabled(): Boolean = equalizerPrefsUseCase.isEqualizerEnabled()

    fun setEqualizerEnabled(enabled: Boolean) {
        equalizer.setEnabled(enabled)
        equalizerPrefsUseCase.setEqualizerEnabled(enabled)
    }

    fun getBassStrength(): Int = equalizer.getBassBoostStrength()

    fun setBassStrength(value: Int) {
        equalizer.setBassBoostStrength(value)
    }

    fun getVirtualizerStrength(): Int = equalizer.getVirtualizerStrength()

    fun setVirtualizerStrength(value: Int) {
        equalizer.setVirtualizerStrength(value)
    }

    fun getBandStep(): Float {
        return .1f
    }

    fun deleteCurrentPreset() = viewModelScope.launch(Dispatchers.IO) {
        val currentPreset = currentPresetLiveData.value!!
        equalizerPrefsUseCase.setCurrentPresetId(0)
        equalizerGateway.deletePreset(currentPreset)
    }

    suspend fun addPreset(title: String): Boolean = withContext(Dispatchers.IO){
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

    fun updateCurrentPresetIfCustom() = viewModelScope.launch(Dispatchers.IO) {
        equalizer.updateCurrentPresetIfCustom()
    }

}
