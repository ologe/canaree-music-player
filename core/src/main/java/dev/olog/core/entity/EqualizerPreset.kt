package dev.olog.core.entity

class EqualizerPreset(
    @JvmField
    val id: Long,
    @JvmField
    val name: String,
    @JvmField
    val bands: List<EqualizerBand>,
    @JvmField
    val isCustom: Boolean
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EqualizerPreset

        if (id != other.id) return false
        if (name != other.name) return false
        if (bands != other.bands) return false
        if (isCustom != other.isCustom) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + bands.hashCode()
        result = 31 * result + isCustom.hashCode()
        return result
    }

    fun withBands(bands: List<EqualizerBand>): EqualizerPreset {
        return EqualizerPreset(
            id = id,
            name = name,
            bands = bands,
            isCustom = isCustom
        )
    }

}

class EqualizerBand(
    @JvmField
    val gain: Float,
    @JvmField
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EqualizerBand

        if (gain != other.gain) return false
        if (frequency != other.frequency) return false

        return true
    }

    override fun hashCode(): Int {
        var result = gain.hashCode()
        result = 31 * result + frequency.hashCode()
        return result
    }


}