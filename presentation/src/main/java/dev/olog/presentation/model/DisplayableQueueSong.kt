package dev.olog.presentation.model

import dev.olog.core.MediaId
import dev.olog.shared.TextUtils

class DisplayableQueueSong(
    override val type: Int,
    override val mediaId: MediaId,
    @JvmField
    val title: String,
    @JvmField
    val artist: String,
    @JvmField
    val album: String,
    @JvmField
    val idInPlaylist: Int,
    @JvmField
    val relativePosition: String,
    @JvmField
    val isCurrentSong: Boolean

) : BaseModel {

    val subtitle = "$artist${TextUtils.MIDDLE_DOT_SPACED}$album"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DisplayableQueueSong

        if (type != other.type) return false
        if (mediaId != other.mediaId) return false
        if (title != other.title) return false
        if (artist != other.artist) return false
        if (album != other.album) return false
        if (idInPlaylist != other.idInPlaylist) return false
        if (relativePosition != other.relativePosition) return false
        if (isCurrentSong != other.isCurrentSong) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type
        result = 31 * result + mediaId.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + album.hashCode()
        result = 31 * result + idInPlaylist
        result = 31 * result + relativePosition.hashCode()
        result = 31 * result + isCurrentSong.hashCode()
        return result
    }


}