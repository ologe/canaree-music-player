package dev.olog.presentation.model

import android.content.res.Resources
import dev.olog.core.MediaId
import dev.olog.presentation.R
import dev.olog.shared.TextUtils

sealed class DisplayableItem(
    @JvmField
    override val type: Int,
    @JvmField
    override val mediaId: MediaId
) : BaseModel

class DisplayableTrack(
    @JvmField
    override val type: Int,
    @JvmField
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
    val dataModified: Long

) : DisplayableItem(type, mediaId) {

    @JvmField
    val subtitle = "$artist${TextUtils.MIDDLE_DOT_SPACED}$album"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DisplayableTrack

        if (type != other.type) return false
        if (mediaId != other.mediaId) return false
        if (title != other.title) return false
        if (artist != other.artist) return false
        if (album != other.album) return false
        if (idInPlaylist != other.idInPlaylist) return false
        if (dataModified != other.dataModified) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type
        result = 31 * result + mediaId.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + album.hashCode()
        result = 31 * result + idInPlaylist
        result = 31 * result + dataModified.hashCode()
        return result
    }


}

class DisplayableAlbum(
    @JvmField
    override val type: Int,
    @JvmField
    override val mediaId: MediaId,
    @JvmField
    val title: String,
    @JvmField
    val subtitle: String
) : DisplayableItem(type, mediaId) {

    companion object {
        @JvmStatic
        fun readableSongCount(resources: Resources, size: Int): String {
            if (size <= 0) {
                return ""
            }
            return resources.getQuantityString(R.plurals.common_plurals_song, size, size)
                .toLowerCase()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DisplayableAlbum

        if (type != other.type) return false
        if (mediaId != other.mediaId) return false
        if (title != other.title) return false
        if (subtitle != other.subtitle) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type
        result = 31 * result + mediaId.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + subtitle.hashCode()
        return result
    }


}

class DisplayableHeader(
    @JvmField
    override val type: Int,
    @JvmField
    override val mediaId: MediaId,
    @JvmField
    val title: String,
    @JvmField
    val subtitle: String? = null,
    @JvmField
    val visible: Boolean = true

) : DisplayableItem(type, mediaId) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DisplayableHeader

        if (type != other.type) return false
        if (mediaId != other.mediaId) return false
        if (title != other.title) return false
        if (subtitle != other.subtitle) return false
        if (visible != other.visible) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type
        result = 31 * result + mediaId.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + (subtitle?.hashCode() ?: 0)
        result = 31 * result + visible.hashCode()
        return result
    }
}


class DisplayableNestedListPlaceholder(
    @JvmField
    override val type: Int,
    @JvmField
    override val mediaId: MediaId
) : DisplayableItem(type, mediaId) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DisplayableNestedListPlaceholder

        if (type != other.type) return false
        if (mediaId != other.mediaId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type
        result = 31 * result + mediaId.hashCode()
        return result
    }
}