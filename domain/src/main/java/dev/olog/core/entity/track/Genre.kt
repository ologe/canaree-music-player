package dev.olog.core.entity.track

import dev.olog.core.mediaid.MediaId
import dev.olog.core.mediaid.MediaIdCategory

data class Genre(
    val id: Long,
    val name: String,
    val size: Int
) {

    fun getMediaId(): MediaId {
        return MediaId.createCategoryValue(MediaIdCategory.GENRES, id.toString())
    }

}