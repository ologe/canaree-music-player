package dev.olog.offlinelyrics.model

class LyricsModel(
    @JvmField
    val id: Long,
    @JvmField
    val lyrics: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LyricsModel

        if (id != other.id) return false
        if (lyrics != other.lyrics) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + lyrics.hashCode()
        return result
    }
}