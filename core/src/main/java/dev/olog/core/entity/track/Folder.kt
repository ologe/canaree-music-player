package dev.olog.core.entity.track

import dev.olog.core.MediaId.Category
import dev.olog.core.MediaIdCategory.FOLDERS

data class Folder(
    val id: Long,
    val title: String,
    val path: String,
    val size: Int
) {

    fun getMediaId(): Category {
        return Category(FOLDERS, id)
    }

}