package dev.olog.core.entity.track

import dev.olog.core.MediaId
import dev.olog.core.entity.track.AutoPlaylist.Id.*

data class AutoPlaylist(
    val id: Id,
    val title: String,
    val size: Int,
) {
    enum class Id(val key: Long) {
        SongLastAdded(0),
        SongFavorites(1),
        SongHistory(2),
        PodcastLastAdded(3),
        PodcastFavorites(4),
        PodcastHistory(5);

        val isLastAdded: Boolean
            get() = this in arrayOf(SongLastAdded, PodcastLastAdded)

        val isFavorite: Boolean
            get() = this in arrayOf(SongFavorites, PodcastFavorites)
    }

    companion object {
        fun findPlaylistId(id: Long): Id? {
            return values().find { it.key == id }
        }
    }

    fun getMediaId(): MediaId {
        return MediaId.ofAutoPlaylist(id, isPodcast)
    }

    val isPodcast: Boolean
        get() = id in arrayOf(PodcastLastAdded, PodcastFavorites, PodcastHistory)

    val isLastAdded: Boolean
        get() = id.isLastAdded

    val isFavorite: Boolean
        get() = id.isFavorite

}