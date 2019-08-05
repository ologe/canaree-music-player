package dev.olog.core.entity

class OfflineLyrics(
    @JvmField
    val trackId: Long,
    @JvmField
    val lyrics: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OfflineLyrics

        if (trackId != other.trackId) return false
        if (lyrics != other.lyrics) return false

        return true
    }

    override fun hashCode(): Int {
        var result = trackId.hashCode()
        result = 31 * result + lyrics.hashCode()
        return result
    }
}