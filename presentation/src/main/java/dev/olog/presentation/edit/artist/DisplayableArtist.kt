package dev.olog.presentation.edit.artist

class DisplayableArtist(
    @JvmField
    val id: Long,
    @JvmField
    val title: String,
    @JvmField
    val albumArtist: String,
    @JvmField
    val songs: Int,
    @JvmField
    val isPodcast: Boolean
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DisplayableArtist

        if (id != other.id) return false
        if (title != other.title) return false
        if (albumArtist != other.albumArtist) return false
        if (songs != other.songs) return false
        if (isPodcast != other.isPodcast) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + albumArtist.hashCode()
        result = 31 * result + songs
        result = 31 * result + isPodcast.hashCode()
        return result
    }

    fun with(
        id: Long = this.id,
        title: String = this.title,
        albumArtist: String = this.albumArtist,
        songs: Int = this.songs,
        isPodcast: Boolean = this.isPodcast
    ): DisplayableArtist {
        return DisplayableArtist(
            id, title, albumArtist, songs, isPodcast
        )
    }

}