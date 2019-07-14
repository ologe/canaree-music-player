package dev.olog.presentation.model

import android.content.res.Resources
import android.os.Bundle
import dev.olog.core.MediaId
import dev.olog.presentation.R
import dev.olog.shared.utils.TextUtils

data class DisplayableItem(
    override val type: Int,
    override val mediaId: MediaId,
    val title: String,
    val subtitle: String? = null,
    val isPlayable: Boolean = false,
    val idInPlaylist: Long? = null,
    val extra: Bundle? = null

) : BaseModel {

    companion object {

        fun handleSongListSize(resources: Resources, size: Int): String {
            if (size <= 0) {
                return ""
            }
            return resources.getQuantityString(R.plurals.common_plurals_song, size, size)
                .toLowerCase()
        }

        fun handleAlbumListSize(resources: Resources, size: Int): String {
            if (size <= 0) {
                return ""
            }
            return resources.getQuantityString(R.plurals.common_plurals_album, size, size)
                .toLowerCase()
        }

    }

}


sealed class DisplayableItem2(
    override val type: Int,
    override val mediaId: MediaId
) : BaseModel

data class DisplayableTrack(
    override val type: Int,
    override val mediaId: MediaId,
    val title: String,
    val artist: String,
    val album: String,
    val idInPlaylist: Int

) : DisplayableItem2(type, mediaId) {

    val subtitle = "$artist${TextUtils.MIDDLE_DOT_SPACED}$album"

}

data class DisplayableAlbum(
    override val type: Int,
    override val mediaId: MediaId,
    val title: String,
    val subtitle: String
) : DisplayableItem2(type, mediaId)

data class DisplayableHeader(
    override val type: Int,
    override val mediaId: MediaId,
    val title: String,
    val visible: Boolean = true

) : DisplayableItem2(type, mediaId)

data class DisplayableNestedListPlaceholder(
    override val type: Int,
    override val mediaId: MediaId

) : DisplayableItem2(type, mediaId)