package dev.olog.shared_android

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileNotFoundException

object ImagesFolderUtils {

    private val COVER_URI = Uri.parse("content://media/external/audio/albumart")

    const val FOLDER = "folder"
    const val PLAYLIST = "playlist"
    const val ALBUM = "album"
    const val ARTIST = "artist"
    const val GENRE = "genre"
    private const val NEURAL = "_neural"

    fun getOriginalAlbumCover(albumId: Long) : Uri {
        return ContentUris.withAppendedId(COVER_URI, albumId)
    }

    fun getNeuralAlbumCover(context: Context, albumId: Long): Uri{
        val neuralFolder = getImageFolderFor(context, ALBUM + NEURAL)
        val image = findImage(neuralFolder, albumId.toString())
        if (image != null){
            return Uri.fromFile(File(image))
        }
        throw FileNotFoundException()
    }

    fun getFolderName(folderName: String): String {
        if (Constants.useNeuralImages){
            return folderName + NEURAL
        }
        return folderName
    }

    fun forFolder(context: Context, folderPath: String): String{
        val normalizedPath = folderPath.replace(File.separator, "")
        return getImageImpl(context, FOLDER, normalizedPath)
    }

    fun forPlaylist(context: Context, playlistId: Long): String{
        return getImageImpl(context, PLAYLIST, playlistId.toString())
    }

    fun forAlbum(context: Context, albumId: Long): String {
        return getAlbumImageImpl(context, albumId.toString())
    }

    fun forArtist(context: Context, artistId: Long): String {
        return getImageImpl(context, ARTIST, artistId.toString())
    }

    fun forGenre(context: Context, genreId: Long): String {
        return getImageImpl(context, GENRE, genreId.toString())
    }

    private fun getAlbumImageImpl(context: Context, albumId: String): String {
        if (Constants.useNeuralImages){
            val neuralFolder = getImageFolderFor(context, ALBUM + NEURAL)
            val image = findImage(neuralFolder, albumId)
            if (image != null){
                return image
            }
        }
        return getOriginalAlbumCover(albumId.toLong()).toString()
    }

    private fun getImageImpl(context: Context, parent: String, child: String): String {
        if (Constants.useNeuralImages){
            val neuralFolder = getImageFolderFor(context, parent + NEURAL)
            val image = findImage(neuralFolder, child)
            if (image != null){
                return image
            }
        }
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
