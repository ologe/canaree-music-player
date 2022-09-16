package dev.olog.core.entity.track

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory

data class Playlist(
    val id: String,
    val title: String,
    val path: String,
    val size: Int,
    val isPodcast: Boolean
) {

    fun getMediaId(): MediaId {
        val category =
            if (isPodcast) MediaIdCategory.PODCASTS_PLAYLIST else MediaIdCategory.PLAYLISTS
        return MediaId.createCategoryValue(category, id)
    }

}