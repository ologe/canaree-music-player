package dev.olog.media.model

import dev.olog.core.MediaId

class PlayerItem(
    @JvmField
    val mediaId: MediaId,
    @JvmField
    val title: String,
    @JvmField
    val artist: String,
    @JvmField
    val idInPlaylist: Long
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlayerItem

        if (mediaId != other.mediaId) return false
        if (title != other.title) return false
        if (artist != other.artist) return false
        if (idInPlaylist != other.idInPlaylist) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mediaId.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + idInPlaylist.hashCode()
        return result
    }
}