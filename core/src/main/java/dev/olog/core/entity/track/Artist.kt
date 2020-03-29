package dev.olog.core.entity.track

import dev.olog.core.MediaId.Category
import dev.olog.core.MediaIdCategory.ARTISTS
import dev.olog.core.MediaIdCategory.PODCASTS_AUTHORS

data class Artist(
    val id: Long,
    val name: String,
    val albumArtist: String,
    val songs: Int,
    val isPodcast: Boolean
) {

    val mediaId: Category
        get() {
            val category = if (isPodcast) PODCASTS_AUTHORS else ARTISTS
            return Category(category, "${this.id}")
        }
}