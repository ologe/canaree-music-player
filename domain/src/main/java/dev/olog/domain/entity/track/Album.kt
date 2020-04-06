package dev.olog.domain.entity.track

import dev.olog.domain.MediaId.Category
import dev.olog.domain.MediaIdCategory.ALBUMS
import dev.olog.domain.MediaIdCategory.ARTISTS

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
        get() =  Category(ALBUMS, "${this.id}")

    val artistMediaId: Category
        get() = Category(ARTISTS, "${this.artistId}")

}