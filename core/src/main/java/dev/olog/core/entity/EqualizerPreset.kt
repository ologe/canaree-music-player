package dev.olog.core.entity

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

data class EqualizerBand(
    val gain: Float,
    val frequency: Float
) {

    val displayableFrequency: String
        get() {
            val freq = frequency.toInt()
            if (frequency >= 1000) {
                return "${freq.toString().dropLast(3)}K"
            }
            return freq.toString()
        }

}