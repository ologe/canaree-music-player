package dev.olog.core.entity.track

import dev.olog.core.mediaid.MediaId
import dev.olog.core.mediaid.MediaIdCategory

data class Playlist(
    val id: Long,
    val title: String,
    val size: Int,
    val isPodcast: Boolean
) {

    fun getMediaId(): MediaId {
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