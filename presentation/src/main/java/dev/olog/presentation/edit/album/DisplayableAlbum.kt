package dev.olog.presentation.edit.album

class DisplayableAlbum(
    @JvmField
    val id: Long,
    @JvmField
    val title: String,
    @JvmField
    val artist: String,
    @JvmField
    val albumArtist: String,
    @JvmField
    val genre: String,
    @JvmField
    val year: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DisplayableAlbum

        if (id != other.id) return false
        if (title != other.title) return false
        if (artist != other.artist) return false
        if (albumArtist != other.albumArtist) return false
        if (genre != other.genre) return false
        if (year != other.year) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + albumArtist.hashCode()
        result = 31 * result + genre.hashCode()
        result = 31 * result + year.hashCode()
        return result
    }
}