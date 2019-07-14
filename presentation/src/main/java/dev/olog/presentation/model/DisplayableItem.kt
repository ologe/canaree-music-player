package dev.olog.presentation.model

import android.content.res.Resources
import android.os.Bundle
import dev.olog.core.MediaId
import dev.olog.presentation.R

data class DisplayableItem (
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
            if (size <= 0){
                return ""
            }
            return resources.getQuantityString(R.plurals.common_plurals_song, size, size).toLowerCase()
        }

        fun handleAlbumListSize(resources: Resources, size: Int): String {
            if (size <= 0){
                return ""
            }
            return resources.getQuantityString(R.plurals.common_plurals_album, size, size).toLowerCase()
        }

    }

}