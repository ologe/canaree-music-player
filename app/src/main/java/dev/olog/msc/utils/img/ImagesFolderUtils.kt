package dev.olog.msc.utils.img

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.utils.k.extension.clamp
import java.io.File

object ImagesFolderUtils {

    private val COVER_URI = Uri.parse("content://media/external/audio/albumart")

    const val FOLDER = "folder"
    const val PLAYLIST = "playlist"
    const val GENRE = "genre"
    private const val DEBUG = "_debug"

    fun getFolderName(folderName: String): String {
        if (AppConstants.useFakeData){
            return "$folderName$DEBUG"
        }
        return folderName
    }

    fun forFolder(context: Context, folderPath: String): String{
        val normalizedPath = folderPath.replace(File.separator, "")
        if (AppConstants.useFakeData){
            return getImageImpl(context, "$FOLDER$DEBUG", normalizedPath)
        }
        return getImageImpl(context, FOLDER, normalizedPath)
    }

    fun forPlaylist(context: Context, playlistId: Long): String{
        if (AppConstants.useFakeData){
            return getImageImpl(context, "$PLAYLIST$DEBUG", playlistId.toString())
        }
        return getImageImpl(context, PLAYLIST, playlistId.toString())
    }

    fun forAlbum(albumId: Long): String {
        if (AppConstants.useFakeData){
            return getFakeImage(albumId)
        }
        return ContentUris.withAppendedId(COVER_URI, albumId).toString()
    }
    private fun getFakeImage(albumId: Long): String {
        val safe = clamp(albumId.rem(10), 0, 10)
        return Uri.parse("file:///android_asset/images/$safe.jpg").toString()
    }

    fun forGenre(context: Context, genreId: Long): String {
        if (AppConstants.useFakeData){
            return getImageImpl(context, "$GENRE$DEBUG", genreId.toString())
        }
        return getImageImpl(context, GENRE, genreId.toString())
    }

    private fun getImageImpl(context: Context, parent: String, child: String): String {
        val folder = getImageFolderFor(context, parent)
        return findImage(folder, child) ?: ""
    }

    private fun findImage(directory: File, childId: String): String? {
        for (child in directory.listFiles()) {
            val indexOfUnderscore = child.name.indexOf("_")
            if (indexOfUnderscore != -1){
                val searchedName = child.name.substring(0, indexOfUnderscore)
                if (searchedName == childId){
                    return child.path
                }
            }
        }
        return null
    }

    fun getImageFolderFor(context: Context, entity: String): File {
        val folder = File("${context.applicationInfo.dataDir}${File.separator}$entity")
        if (!folder.exists()){
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

    fun containedAlbums(): List<Long> {
        val indexOfStart = name.indexOf("(") + 1
        val indexOfEnd = name.indexOf(")")
        return name.substring(indexOfStart, indexOfEnd)
                .split("_")
                .map { it.toLong() }
    }

    fun progressive(): Long {
        val indexOfStart = name.indexOf("_") + 1
        val indexOfEnd = name.indexOf("(")
        return name.substring(indexOfStart, indexOfEnd).toLong()
    }

}
