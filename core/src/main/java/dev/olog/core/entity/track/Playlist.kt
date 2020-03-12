package dev.olog.core.entity.track

import dev.olog.core.MediaId.Category
import dev.olog.core.MediaIdCategory.PLAYLISTS
import dev.olog.core.MediaIdCategory.PODCASTS_PLAYLIST

data class Playlist(
    val id: Long,
    val title: String,
    val size: Int,
    val isPodcast: Boolean
) {

    val mediaId: Category
        get() {
            val category = if (isPodcast) PODCASTS_PLAYLIST else PLAYLISTS
            return Category(category, id)
        }

}