package dev.olog.domain.entity.track

import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.mediaid.MediaIdCategory

data class Genre(
    val id: Long,
    val name: String,
    val size: Int
) {

    fun getMediaId(): MediaId.Category {
        return MediaId.createCategoryValue(MediaIdCategory.GENRES, id.toString())
    }

}