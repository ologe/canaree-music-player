package dev.olog.core.entity.track

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory

class Artist(
    @JvmField
    val id: Long,
    @JvmField
    val name: String,
    @JvmField
    val albumArtist: String,
    @JvmField
    val songs: Int,
    @JvmField
    val albums: Int,
    @JvmField
    val isPodcast: Boolean
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Artist

        if (id != other.id) return false
        if (name != other.name) return false
        if (albumArtist != other.albumArtist) return false
        if (songs != other.songs) return false
        if (albums != other.albums) return false
        if (isPodcast != other.isPodcast) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + albumArtist.hashCode()
        result = 31 * result + songs
        result = 31 * result + albums
        result = 31 * result + isPodcast.hashCode()
        return result
    }

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
            albums = albums,
            isPodcast = isPodcast
        )
    }

}