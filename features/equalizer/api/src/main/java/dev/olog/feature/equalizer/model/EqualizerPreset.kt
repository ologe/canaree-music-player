package dev.olog.feature.equalizer.model

data class EqualizerPreset(
    val id: Long,
    val name: String,
    val bands: List<EqualizerBand>,
    val isCustom: Boolean
) {

    fun withBands(bands: List<EqualizerBand>): EqualizerPreset {
        return EqualizerPreset(
            id = id,
            name = name,
            bands = bands,
            isCustom = isCustom
        )
    }

}