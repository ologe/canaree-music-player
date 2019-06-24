package dev.olog.core.entity.track

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory

data class Genre (
        val id: Long,
        val name: String,
        val size: Int
)

fun Genre.getMediaId(): MediaId {
    return MediaId.createCategoryValue(MediaIdCategory.GENRES, id.toString())
}