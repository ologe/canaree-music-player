package dev.olog.domain.entity.track

import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.mediaid.MediaIdCategory

data class Playlist(
    val id: Long,
    val title: String,
    val size: Int,
    val isPodcast: Boolean
) {

    fun getMediaId(): MediaId.Category {
        val category =
            if (isPodcast) MediaIdCategory.PODCASTS_PLAYLIST else MediaIdCategory.PLAYLISTS
        return MediaId.createCategoryValue(category, id.toString())
    }

    fun withSongs(songs: Int): Playlist {
        return Playlist(
            id = id,
            title = title,
            size = songs,
            isPodcast = isPodcast
        )
    }

}