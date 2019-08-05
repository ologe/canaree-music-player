package dev.olog.core.entity.track

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory

class Album(
    @JvmField
    val id: Long,
    @JvmField
    val artistId: Long,
    @JvmField
    val title: String,
    @JvmField
    val artist: String,
    @JvmField
    val albumArtist: String,
    @JvmField
    val songs: Int,
    @JvmField
    val hasSameNameAsFolder: Boolean,
    @JvmField
    val isPodcast: Boolean
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Album

        if (id != other.id) return false
        if (artistId != other.artistId) return false
        if (title != other.title) return false
        if (artist != other.artist) return false
        if (albumArtist != other.albumArtist) return false
        if (songs != other.songs) return false
        if (hasSameNameAsFolder != other.hasSameNameAsFolder) return false
        if (isPodcast != other.isPodcast) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + artistId.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + albumArtist.hashCode()
        result = 31 * result + songs
        result = 31 * result + hasSameNameAsFolder.hashCode()
        result = 31 * result + isPodcast.hashCode()
        return result
    }

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