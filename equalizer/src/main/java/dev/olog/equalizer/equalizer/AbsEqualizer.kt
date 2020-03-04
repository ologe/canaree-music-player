package dev.olog.equalizer.equalizer

import dev.olog.core.entity.EqualizerPreset
import dev.olog.core.gateway.EqualizerGateway
import dev.olog.core.prefs.EqualizerPreferencesGateway
import dev.olog.core.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

abstract class AbsEqualizer(
    protected val gateway: EqualizerGateway,
    protected val prefs: EqualizerPreferencesGateway,
    private val schedulers: Schedulers
) : IEqualizerInternal {

    override fun getPresets(): List<EqualizerPreset> = gateway.getPresets()

    override fun observeCurrentPreset(): Flow<EqualizerPreset> {
        return gateway.observeCurrentPreset()
    }

    override fun getCurrentPreset(): EqualizerPreset {
        return gateway.getCurrentPreset()
    }

    override suspend fun updateCurrentPresetIfCustom() = withContext(schedulers.io) {
        var preset = gateway.getCurrentPreset()
        if (preset.isCustom) {
            preset = preset.copy(
                bands = getAllBandsCurrentLevel()
            )
            gateway.updatePreset(preset)
        }
    }

}