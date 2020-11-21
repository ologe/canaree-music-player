package dev.olog.core.entity.track

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory

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