package dev.olog.core.entity.track

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory

data class Folder(
    val title: String, // todo rename to name
    val path: String,
    val size: Int,
) {

    fun getMediaId(): MediaId {
        return MediaId.createCategoryValue(MediaIdCategory.FOLDERS, this.path)
    }

}