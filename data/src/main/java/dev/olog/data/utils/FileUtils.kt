package dev.olog.data.utils

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import dev.olog.data.ImageUtils
import dev.olog.domain.entity.Song
import dev.olog.shared_android.assertBackgroundThread
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

    /**
     * returns true if a new image is created
     */
    fun makeImages(context: Context, songList: List<Song>, parentFolder: String, itemId: String) : Boolean{
       return makeImages2(context, songList.map { it.albumId }, parentFolder, itemId)
    }

    /**
     * returns true if a new image is created
     */
    fun makeImages2(context: Context, albumIdList: List<Long>, parentFolder: String, itemId: String) : Boolean {
        assertBackgroundThread()

        val imageName = "${context.applicationInfo.dataDir}${File.separator}$parentFolder${File.separator}$itemId"
        val file = File(imageName)
        if (file.exists()){
            return false
        }

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

        return doSomething(context, uris, parentFolder, itemId)
    }

    private fun idToUri(albumId: Long): Uri {
        return ContentUris.withAppendedId(COVER_URI, albumId)
    }

    private fun doSomething(context: Context, uris: List<IdWithBitmap>, parentFolder: String, itemId: String) : Boolean {
        if (uris.isEmpty()) {
            // new image is empty, delete old
            val parentFile = File("${context.applicationInfo.dataDir}${File.separator}$parentFolder")
            if (parentFile.exists()){
                val alreadyExistingFile = parentFile
                        .listFiles().firstOrNull { it.name.substring(0, it.name.indexOf("_")) == itemId }
                alreadyExistingFile?.delete()
            }

            return false
        }

        val albumsId = uris.map { it.id }
        // ref to cartella con le immagini
        val parentFile = File("${context.applicationInfo.dataDir}${File.separator}$parentFolder")
        parentFile.mkdirs()

        // cerco se esiste gia un file
        val alreadyExistingFile = parentFile
                .listFiles().firstOrNull { it.name.substring(0, it.name.indexOf("_")) == itemId }

        if (alreadyExistingFile != null){ // esiste
//            Log.w("fileUtils", "image found for $parentFolder $itemId")
            val fileName = alreadyExistingFile.name

            val albumIdsInFilename = fileName.substring(
                    fileName.indexOf("(") + 1,
                    fileName.indexOf(")")
            ).split("_").map { it.toLong() }

            if (albumsId.sorted() == albumIdsInFilename.sorted()){
//                Log.w("fileUtils", "same image, do nothing for $parentFolder $itemId")
                // same image, abort
                return false
            } else {
//                Log.w("fileUtils", "images are diffrent, update for $parentFolder $itemId")
                val progr = fileName.substring(
                        fileName.indexOf("_") + 1,
                        fileName.indexOf("(")
                ).toInt()

                // image already exist, create new with new progr
                alreadyExistingFile.delete() // first delete old
                prepareSaveThenSave(context, uris, parentFolder, itemId, albumsId, progr + 1)
            }
        } else {
//            Log.w("fileUtils", "create brand new image for $parentFolder $itemId")
            // create new image
            prepareSaveThenSave(context, uris, parentFolder, itemId, albumsId, 1)
        }
        return true
    }

    private fun prepareSaveThenSave(context: Context, uris: List<IdWithBitmap>, parentFolder: String, itemId: String,
                                    albumsId: List<Long>, progr: Int){
        val bitmap = ImageUtils.joinImages(uris.map { it.bitmap })
        val newFileName = "${itemId}_$progr${albumsId.joinToString(separator = "_", prefix = "(", postfix = ")")}"
        FileUtils.saveFile(context, parentFolder, newFileName, bitmap)
    }

}

private class IdWithBitmap(
        context: Context,
        uri: Uri
) {

    val id : Long = ContentUris.parseId(uri)
    val bitmap : Bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)

}