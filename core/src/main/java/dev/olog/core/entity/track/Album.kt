package dev.olog.core.entity.track

import dev.olog.core.MediaId.Category
import dev.olog.core.MediaIdCategory.ALBUMS
import dev.olog.core.MediaIdCategory.ARTISTS

data class Album(
    val id: Long,
    val artistId: Long,
    val title: String,
    val artist: String,
    val albumArtist: String,
    val songs: Int,
    val hasSameNameAsFolder: Boolean
) {

    fun getMediaId(): Category {
        return Category(ALBUMS, this.id)
    }

    fun getArtistMediaId(): Category {
        return Category(ARTISTS, this.artistId)
    }

}