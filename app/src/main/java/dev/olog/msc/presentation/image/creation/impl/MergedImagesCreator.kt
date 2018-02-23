package dev.olog.msc.presentation.image.creation.impl

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.utils.assertBackgroundThread
import dev.olog.msc.utils.img.ImageUtils
import dev.olog.msc.utils.img.ImagesFolderUtils
import dev.olog.msc.utils.img.extractImageName
import java.io.File
import java.io.FileOutputStream

object MergedImagesCreator {

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
                .mapNotNull {
                    try {
                        val bitmap = getBitmap(context, it)
                        IdWithBitmap(it, bitmap)
                    } catch (ex: Exception) { null }
                }
                .take(9)
                .toList()

        return doSomething(context, uris, parentFolder, itemId)
    }

    private fun getBitmap(context: Context, albumId: Long): Bitmap {
        var bitmap = BitmapLruCache.get(albumId)
        if (bitmap == null){
            val uri = ImagesFolderUtils.forAlbum(albumId)
            bitmap = ImageUtils.getBitmapFromUriOrNull(context, Uri.parse(uri), 500, 500)!!
            BitmapLruCache.put(albumId, bitmap)
        }

        return bitmap
    }

    private fun doSomething(context: Context, uris: List<IdWithBitmap>, parentFolder: String, itemId: String) : Boolean {
        val imageDirectory = ImagesFolderUtils.getImageFolderFor(context, parentFolder)

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
            val fileImageName = oldImage.extractImageName()

            val albumIdsInFilename = fileImageName.containedAlbums()

            val sameImages = albumsId.sorted() == albumIdsInFilename.sorted()
            if (sameImages){
                // same image, exit
                return false
            } else {
                // images are different
                val progr = fileImageName.progressive()

                // image already exist, create new with a new progr
                oldImage.delete() // first delete old
                prepareSaveThenSave(uris, imageDirectory, itemId, albumsId, progr + 1L)
            }
        } else {
            // create new image
            prepareSaveThenSave(uris, imageDirectory, itemId, albumsId, System.currentTimeMillis())
        }
        return true
    }

    private fun prepareSaveThenSave(uris: List<IdWithBitmap>, directory: File, itemId: String,
                                    albumsId: List<Long>, progr: Long){
        val bitmap = MergedImageUtils.joinImages(uris.map { it.bitmap })
        val child = ImagesFolderUtils.createFileName(itemId, progr, albumsId)
        saveFile(directory, child, bitmap)
    }

    private fun saveFile(directory: File, child: String, bitmap: Bitmap)  {
        assertBackgroundThread()

        val dest = File(directory, "$child.webp")
        val out = FileOutputStream(dest)
        bitmap.compress(Bitmap.CompressFormat.WEBP, 90, out)
        out.close()
        bitmap.recycle()
    }

}

private class IdWithBitmap(
        val id: Long,
        val bitmap: Bitmap
)