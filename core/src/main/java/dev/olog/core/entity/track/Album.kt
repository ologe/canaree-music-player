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

    val mediaId: Category
        get() =  Category(ALBUMS, this.id)

    val artistMediaId: Category
        get() = Category(ARTISTS, this.artistId)

}