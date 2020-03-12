package dev.olog.core.entity.track

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory

data class Folder(
    val id: Long,
    val title: String,
    val path: String,
    val size: Int
) {

    fun getMediaId(): MediaId {
        return MediaId.createCategoryValue(MediaIdCategory.FOLDERS, id)
    }

}