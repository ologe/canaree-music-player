package dev.olog.core.entity.track

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory

data class Artist(
    val id: Long,
    val name: String,
    val albumArtist: String,
    val songs: Int,
    val isPodcast: Boolean
) {

    companion object

    fun getMediaId(): MediaId {
        val category = if (isPodcast) MediaIdCategory.PODCASTS_ARTISTS else MediaIdCategory.ARTISTS
        return MediaId.createCategoryValue(category, this.id.toString())
    }

    fun withSongs(songs: Int): Artist {
        return Artist(
            id = id,
            name = name,
            albumArtist = albumArtist,
            songs = songs,
            isPodcast = isPodcast
        )
    }
}

val Artist.Companion.EMPTY: Artist
    get() = Artist(
        id = 0,
        name = "",
        albumArtist = "",
        songs = 0,
        isPodcast = false
    )
