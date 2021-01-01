package dev.olog.domain.entity.track

import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.mediaid.MediaIdCategory

data class Album(
    val id: Long,
    val artistId: Long,
    val title: String,
    val artist: String,
    val albumArtist: String,
    val songs: Int,
    val hasSameNameAsFolder: Boolean,
    val isPodcast: Boolean
) {

    companion object

    val hasUnknownTitle: Boolean
        get() = this.title == "<unknown>"

    val hasUnknownArtist: Boolean
        get() = this.artist == "<unknown>"

    fun getMediaId(): MediaId {
        val category = if (isPodcast) MediaIdCategory.PODCASTS_ALBUMS else MediaIdCategory.ALBUMS
        return MediaId.createCategoryValue(category, this.id.toString())
    }

    fun getArtistMediaId(): MediaId {
        val category = if (isPodcast) MediaIdCategory.PODCASTS_ARTISTS else MediaIdCategory.ARTISTS
        return MediaId.createCategoryValue(category, this.artistId.toString())
    }

    fun withSongs(songs: Int): Album {
        return Album(
            id = id,
            artistId = artistId,
            title = title,
            artist = artist,
            albumArtist = albumArtist,
            songs = songs,
            hasSameNameAsFolder = hasSameNameAsFolder,
            isPodcast = isPodcast
        )
    }

}

val Album.Companion.EMPTY: Album
    get() = Album(
        id = 0,
        artistId = 0,
        title = "",
        artist = "",
        albumArtist = "",
        songs = 0,
        hasSameNameAsFolder = false,
        isPodcast = false
    )
