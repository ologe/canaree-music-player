package dev.olog.domain.entity.track

import dev.olog.domain.MediaId.Category
import dev.olog.domain.MediaIdCategory.PLAYLISTS
import dev.olog.domain.MediaIdCategory.PODCASTS_PLAYLIST

data class Playlist(
    val id: Long,
    val title: String,
    val size: Int,
    val isPodcast: Boolean
) {

    val mediaId: Category
        get() {
            val category = if (isPodcast) PODCASTS_PLAYLIST else PLAYLISTS
            return Category(category, "${this.id}")
        }

}