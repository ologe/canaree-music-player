package dev.olog.presentation.model

import android.content.res.Resources
import dev.olog.core.MediaId
import dev.olog.presentation.R
import dev.olog.shared.android.TextUtils

@Deprecated("")
sealed class DisplayableItem(
    override val type: Int,
    override val mediaId: MediaId
) : BaseModel

@Deprecated("")
data class DisplayableTrack(
    override val type: Int,
    override val mediaId: MediaId,
    val title: String,
    val artist: String,
    val album: String,
    val idInPlaylist: Int,
    val dataModified: Long

) : DisplayableItem(type, mediaId) {

    val subtitle = "$artist${TextUtils.MIDDLE_DOT_SPACED}$album"

}

@Deprecated("")
data class DisplayableAlbum(
    override val type: Int,
    override val mediaId: MediaId,
    val title: String,
    val subtitle: String
) : DisplayableItem(type, mediaId) {

    companion object {
        fun readableSongCount(resources: Resources, size: Int): String {
            if (size <= 0) {
                return ""
            }
            return resources.getQuantityString(R.plurals.common_plurals_song, size, size)
                .toLowerCase()
        }
    }

}

@Deprecated("")
data class DisplayableHeader(
    override val type: Int,
    override val mediaId: MediaId,
    val title: String,
    val subtitle: String? = null,
    val visible: Boolean = true

) : DisplayableItem(type, mediaId)

@Deprecated("")
data class DisplayableNestedListPlaceholder(
    override val type: Int,
    override val mediaId: MediaId
) : DisplayableItem(type, mediaId)