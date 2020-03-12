package dev.olog.core.entity.track

import dev.olog.core.MediaId.Category
import dev.olog.core.MediaIdCategory.GENRES

data class Genre(
    val id: Long,
    val name: String,
    val size: Int
) {

    val mediaId: Category
        get() = Category(GENRES, id)

}