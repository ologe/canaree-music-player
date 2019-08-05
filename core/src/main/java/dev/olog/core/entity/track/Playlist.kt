package dev.olog.core.entity.track

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory

class Playlist(
    @JvmField
    val id: Long,
    @JvmField
    val title: String,
    @JvmField
    val size: Int,
    @JvmField
    val isPodcast: Boolean
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Playlist

        if (id != other.id) return false
        if (title != other.title) return false
        if (size != other.size) return false
        if (isPodcast != other.isPodcast) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + size
        result = 31 * result + isPodcast.hashCode()
        return result
    }

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