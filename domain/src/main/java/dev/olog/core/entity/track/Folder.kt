package dev.olog.core.entity.track

import dev.olog.core.mediaid.MediaId
import dev.olog.core.mediaid.MediaIdCategory

data class Folder(
    val title: String,
    val path: String,
    val size: Int
) {


    fun getMediaId(): MediaId {
        return MediaId.createCategoryValue(MediaIdCategory.FOLDERS, this.path)
    }

}