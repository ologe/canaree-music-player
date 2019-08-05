package dev.olog.presentation.edit.artist

class DisplayableArtist(
    @JvmField
    val id: Long,
    @JvmField
    val title: String,
    @JvmField
    val albumArtist: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DisplayableArtist

        if (id != other.id) return false
        if (title != other.title) return false
        if (albumArtist != other.albumArtist) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + albumArtist.hashCode()
        return result
    }
}