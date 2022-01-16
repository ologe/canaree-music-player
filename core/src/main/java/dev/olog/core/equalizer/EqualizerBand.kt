package dev.olog.core.equalizer

data class EqualizerBand(
    val id: Long,
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