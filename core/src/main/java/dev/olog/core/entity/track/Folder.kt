package dev.olog.core.entity.track

import dev.olog.core.MediaId.Category
import dev.olog.core.MediaIdCategory.FOLDERS

data class Folder(
    val id: Long,
    val title: String,
    val path: String,
    val size: Int
) {

    val mediaId: Category
        get() = Category(FOLDERS, id)

}