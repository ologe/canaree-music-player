package dev.olog.domain.entity

data class EqualizerPreset(
    val id: Long,
    val name: String,
    val bands: List<EqualizerBand>,
    val isCustom: Boolean
)

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