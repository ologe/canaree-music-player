package dev.olog.data.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import dev.olog.data.ImageUtils
import dev.olog.domain.entity.Song
import dev.olog.shared_android.ImagesFolderUtils
import dev.olog.shared_android.assertBackgroundThread
import dev.olog.shared_android.extractImageName
import java.io.File
import java.io.FileOutputStream

object FileUtils {

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

        val uris = albumIdList.asSequence()
                .distinctBy { it }
                .mapNotNull { IdWithBitmap.from(context, it) }
                .take(9)
                .toList()

        return doSomething(context, uris, parentFolder, itemId)
    }

    private fun doSomething(context: Context, uris: List<IdWithBitmap>, parentFolder: String, itemId: String) : Boolean {
        val imageDirectory = ImagesFolderUtils.getImageFolderFor(context, itemId)

        if (uris.isEmpty()) {
            // new requested image has no childs, delete old if exists
            imageDirectory.listFiles()
                    .firstOrNull { it.name.substring(0, it.name.indexOf("_")) == itemId }
                    ?.delete()
            return false
        }

        val albumsId = uris.map { it.id }

        // search for old image
        val oldImage = imageDirectory
                .listFiles().firstOrNull { it.name.substring(0, it.name.indexOf("_")) == itemId }

        if (oldImage != null){ // image found
            val fileName = oldImage.extractImageName()

            val albumIdsInFilename = fileName.containedAlbums()

            if (albumsId.sorted() == albumIdsInFilename.sorted()){
                // same image, exit
                return false
            } else {
                // images are different
                val progr = fileName.progressive()

                // image already exist, create new with a new progr
                oldImage.delete() // first delete old
                prepareSaveThenSave(context, uris, parentFolder, itemId, albumsId, progr + 1)
            }
        } else {
            // create new image
            prepareSaveThenSave(context, uris, parentFolder, itemId, albumsId, 1)
        }
        return true
    }

    private fun prepareSaveThenSave(context: Context, uris: List<IdWithBitmap>, parentFolder: String, itemId: String,
                                    albumsId: List<Long>, progr: Int){
        val bitmap = ImageUtils.joinImages(uris.map { it.bitmap })
        val newFileName = ImagesFolderUtils.createFileName(itemId, progr, albumsId)
        FileUtils.saveFile(context, parentFolder, newFileName, bitmap)
    }

    private fun saveFile(context: Context, parentFolder: String, childName: String, bitmap: Bitmap)  {
        assertBackgroundThread()

        val parentFile = ImagesFolderUtils.getImageFolderFor(context, parentFolder)
        val dest = File(parentFile, childName)
        val out = FileOutputStream(dest)
        bitmap.compress(Bitmap.CompressFormat.WEBP, 85, out)
        out.close()
        bitmap.recycle()
    }

}

private class IdWithBitmap(
        private val context: Context,
        val id: Long,
        private val uri: String
) {

    val bitmap : Bitmap
        get() {
            val file = File(uri)
            val uri = if (file.exists()){
                Uri.fromFile(file)
            } else Uri.parse(uri)
            return MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }

    companion object {
        private fun idToUri(context: Context, albumId: Long): String {
            return ImagesFolderUtils.forAlbum(context, albumId)
        }
        fun from(context: Context, albumId: Long): IdWithBitmap? {
            val uri = idToUri(context, albumId)
            return try {
                IdWithBitmap(context, albumId, uri)
            } catch (ex: Exception){
                null
            }
        }
    }

}