package dev.olog.domain.entity.track

import dev.olog.domain.MediaId.Category
import dev.olog.domain.MediaIdCategory.FOLDERS

data class Folder(
    val title: String,
    val path: String,
    val size: Int
) {

    val id = path

    val mediaId: Category
        get() = Category(FOLDERS, path)

}