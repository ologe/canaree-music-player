package dev.olog.domain.entity.track

import dev.olog.domain.MediaId.Category
import dev.olog.domain.MediaIdCategory.GENRES

data class Genre(
    val id: Long,
    val name: String,
    val size: Int
) {

    val mediaId: Category
        get() = Category(GENRES, "${this.id}")

}