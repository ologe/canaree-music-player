package dev.olog.domain.entity.track

import dev.olog.domain.MediaId.Category
import dev.olog.domain.MediaIdCategory.ALBUMS
import dev.olog.domain.MediaIdCategory.ARTISTS
import java.io.File

data class Album(
    val id: Long,
    val artistId: Long,
    val title: String,
    val artist: String,
    val albumArtist: String,
    val songs: Int,
    val path: String
) {

    val hasSameNameAsFolder: Boolean
        get() {
            val dirName = try {
                val end = path.lastIndexOf(File.separator)
                val before = path.lastIndexOf(File.separator, end - 1)
                path.substring(before + 1, end)
            } catch (ex: Exception){
                ""
            }
            return dirName == title
        }

    val mediaId: Category
        get() =  Category(ALBUMS, "${this.id}")

    val artistMediaId: Category
        get() = Category(ARTISTS, "${this.artistId}")

}