package dev.olog.data.utils

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import dev.olog.data.ImageUtils
import dev.olog.domain.entity.Song
import java.io.File
import java.io.FileOutputStream

object FileUtils {

    private val COVER_URI = Uri.parse("content://media/external/audio/albumart")

    fun saveFile(context: Context, parentFolder: String, fileName: String, bitmap: Bitmap)  {
        assertBackgroundThread()

        val parentFile = File("${context.applicationInfo.dataDir}${File.separator}$parentFolder")
        parentFile.mkdirs()
        val dest = File(parentFile, fileName)
        val out = FileOutputStream(dest)
        bitmap.compress(Bitmap.CompressFormat.WEBP, 85, out)
        out.close()
        bitmap.recycle()
    }

    fun folderImagePath(context: Context, folderPath: String): String {
        return "${context.applicationInfo.dataDir}${File.separator}folder${File.separator}" +
                folderPath.replace(File.separator, "")
    }

    fun playlistImagePath(context: Context, playlistId: Long): String {
        return "${context.applicationInfo.dataDir}${File.separator}playlist${File.separator}$playlistId"
    }

    fun artistImagePath(context: Context, artistId: Long): String {
        return "${context.applicationInfo.dataDir}${File.separator}artist${File.separator}$artistId"
    }

    fun genreImagePath(context: Context, genreId: Long): String {
        return "${context.applicationInfo.dataDir}${File.separator}genre${File.separator}$genreId"
    }

    fun makeImages(context: Context, songList: List<Song>, parentFolder: String, itemId: String) {
        assertBackgroundThread()

        val imageName = "${context.applicationInfo.dataDir}${File.separator}$parentFolder${File.separator}$itemId"
        val file = File(imageName)
        if (file.exists()){
            return
        }

        val uris = songList.asSequence()
                .map { it.albumId }
                .distinctBy { it }
                .map { idToUri(it) }
                .map { try {
                    IdWithBitmap(context, it)
                } catch (ex: Exception){
                    null
                } }
                .filter { it != null }
                .map { it!! }
                .take(9)
                .toList()

        doSomething(context, uris, parentFolder, itemId)
    }

    private fun idToUri(albumId: Long): Uri {
        return ContentUris.withAppendedId(COVER_URI, albumId)
    }

    fun makeImages2(context: Context, albumIdList: List<Long>, parentFolder: String, itemId: String) {
        assertBackgroundThread()

        val uris = albumIdList.asSequence()
                .distinctBy { it }
                .map { idToUri(it) }
                .map { try {
                    IdWithBitmap(context, it)
                } catch (ex: Exception){
                    null
                } }
                .filter { it != null }
                .map { it!! }
                .take(9)
                .toList()

        doSomething(context, uris, parentFolder, itemId)
    }

    private fun doSomething(context: Context, uris: List<IdWithBitmap>, parentFolder: String, itemId: String){
        if (uris.isEmpty()) {
            return
        }

        val albumsId = uris.map { it.id }
        // ref to cartella con le immagini
        val parentFile = File("${context.applicationInfo.dataDir}${File.separator}$parentFolder")

        // cerco se esiste gia un file
        val alreadyExistingFile = parentFile.listFiles()
                .firstOrNull { it.path.contains(itemId ) }

        if (alreadyExistingFile != null){ // esiste
            val fileName = alreadyExistingFile.name

            val albumIdsInFilename = fileName.substring(
                    fileName.indexOf("(") + 1,
                    fileName.indexOf(")")
            ).split("_").map { it.toLong() }

            if (albumsId == albumIdsInFilename){
                // same image, abort
                return
            } else {
                val progr = fileName.substring(
                        fileName.indexOf("_") + 1,
                        fileName.indexOf("(")
                ).toLong()

                // image already exist, create new with new progr
                alreadyExistingFile.delete() // first delete old
                val bitmap = ImageUtils.joinImages(uris.map { it.bitmap })
                val newProgr = progr + 1
                val newFileName = itemId + newProgr + albumsId.joinToString { "_" }
                FileUtils.saveFile(context, parentFolder, newFileName, bitmap)
            }
        } else {
            // create new image
            val bitmap = ImageUtils.joinImages(uris.map { it.bitmap })
            val newProgr = 1
            val fileName = itemId + newProgr + albumsId.joinToString { "_" }
            FileUtils.saveFile(context, parentFolder, fileName, bitmap)
        }
    }

}

private class IdWithBitmap(
        context: Context,
        uri: Uri
) {

    val id : Long = ContentUris.parseId(uri)
    val bitmap : Bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)

}