package dev.olog.msc.presentation.model

import android.content.res.Resources
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseModel
import dev.olog.msc.utils.MediaId

data class DisplayableItem (
        override val type: Int,
        override val mediaId: MediaId,
        val title: String,
        val subtitle: String? = null,
        val image: String = "",
        val isPlayable: Boolean = false,
        val isRemix: Boolean = false,
        val isExplicit: Boolean = false,
        val trackNumber: String = ""

) : BaseModel {

    companion object {

        fun handleSongListSize(resources: Resources, size: Int): String {
            if (size <= 0){
                return ""
            }
            return resources.getQuantityString(R.plurals.song_count, size, size).toLowerCase()
        }

        fun handleAlbumListSize(resources: Resources, size: Int): String {
            if (size <= 0){
                return ""
            }
            return resources.getQuantityString(R.plurals.album_count, size, size).toLowerCase()
        }

    }

}