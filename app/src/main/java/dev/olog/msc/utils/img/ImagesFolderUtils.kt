package dev.olog.msc.utils.img

import android.content.Context
import android.net.Uri
import dev.olog.shared.clamp
import java.io.File

object ImagesFolderUtils {

    const val FOLDER = "folder"
    const val PLAYLIST = "playlist"
    const val GENRE = "genre"

    fun getFolderName(folderName: String): String {
        return folderName
    }

    fun isChoosedImage(image: String): Boolean{
        return image.startsWith("content://com.android.providers.media.documents/document")
    }

    fun forFolder(context: Context, folderPath: String): String{
        val normalizedPath = folderPath.replace(File.separator, "")
        return getImageImpl(context, FOLDER, normalizedPath)
    }

    fun forPlaylist(context: Context, playlistId: Long): String{
        return getImageImpl(context, PLAYLIST, playlistId.toString())
    }

    fun getAssetImage(albumId: Long, songId: Long): String{
        return getFakeImage(albumId, songId)
    }

    fun forAlbum(albumId: Long): String {

//        val uri = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId)
//        val cursor = app.contentResolver.query(uri, arrayOf(MediaStore.Audio.Albums.ALBUM_ART), null,
//                null, null)
//
//        var result : String? = null
//
//        cursor?.use {
//            if (it.moveToFirst()){
//                result = it.getStringOrNull(MediaStore.Audio.Albums.ALBUM_ART)
//            }
//        }
//
//        if (result == null){
//            result = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId).toString()
//        }
//
//        return result!! TODO
        return ""
    }
    private fun getFakeImage(albumId: Long, songId: Long): String {
        val size = 10L
        val safe = clamp((albumId + songId).rem(size), 0, size - 1)
        return Uri.parse("file:///android_asset/images/$safe.jpg").toString()
    }

    fun forGenre(context: Context, genreId: Long): String {
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
