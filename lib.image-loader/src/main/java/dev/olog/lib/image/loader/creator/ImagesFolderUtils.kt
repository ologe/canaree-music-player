package dev.olog.lib.image.loader.creator

import android.content.Context
import java.io.File

object ImagesFolderUtils {

    const val FOLDER = "folder"
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

        // using a builder for readability
        return buildString {
            append(itemId)
            append("_")
            append(progressive)
            append(albumsIdAsString)
        }
    }

}

internal fun File.extractImageName(): ImageName {
    return ImageName(this.name)
}

