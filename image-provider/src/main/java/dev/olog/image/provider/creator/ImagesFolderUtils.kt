package dev.olog.image.provider.creator

import android.content.Context
import java.io.File

object ImagesFolderUtils {

    const val FOLDER = "folder"
    const val SONG = "song"
    const val ALBUM = "album"
    const val ARTIST = "artist"
    const val PLAYLIST = "playlist"
    const val GENRE = "genre"

    fun getImageFolderFor(context: Context, entity: String): File {
        val folder = File("${context.applicationInfo.dataDir}${File.separator}$entity")
        if (!folder.exists()) {
            folder.mkdir()
        }
        return folder
    }

    fun createFileName(itemId: String, progressive: Long, albumsId: List<Long>): String {
        val albumsIdAsString = albumsId.joinToString(
            separator = "_",
            prefix = "(",
            postfix = ")"
        )

        val builder = StringBuilder() // using a builder for readability
            .append(itemId)
            .append("_")
            .append(progressive)
            .append(albumsIdAsString)
        return builder.toString()
    }

}

fun File.extractImageName(): ImageName {
    return ImageName(this)
}

/**
 * File name structure -> artistId_progressive(albumsIdSeparatedByUnderscores).webp
 */
class ImageName(file: File) {

    private val name = file.name

    fun containedAlbums(): List<Long>? {
        try {
            val indexOfStart = name.indexOf("(") + 1
            val indexOfEnd = name.indexOf(")")
            return name.substring(indexOfStart, indexOfEnd)
                .split("_")
                .map { it.toLong() }
        } catch (ex: NumberFormatException){
            ex.printStackTrace()
            return null
        }
    }

    fun progressive(): Long {
        try {
            val indexOfStart = name.indexOf("_") + 1
            val indexOfEnd = name.indexOf("(")
            return name.substring(indexOfStart, indexOfEnd).toLong()
        } catch (ex: Exception){
            ex.printStackTrace()
            return 0
        }
    }

}
