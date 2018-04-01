package dev.olog.msc.presentation.image.creation.impl

import android.content.Context
import android.graphics.Bitmap
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.assertBackgroundThread
import dev.olog.msc.utils.img.ImagesFolderUtils
import dev.olog.msc.utils.img.extractImageName
import dev.olog.msc.utils.k.extension.getBitmapAsync
import java.io.File
import java.io.FileOutputStream

object MergedImagesCreator {

    fun makeImages(context: Context, albumIdList: List<Long>, parentFolder: String, itemId: String) : Boolean {
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

        println("type $parentFolder, current id $itemId, images found ${uris.map { it.id }}")

        return doCreate(context, uris, parentFolder, itemId)
    }

    private fun getBitmap(context: Context, albumId: Long): Bitmap {
        val originalImage = ImagesFolderUtils.forAlbum(albumId)
        val model = DisplayableItem(0, MediaId.albumId(albumId), "", image = originalImage)
        return context.getBitmapAsync(model, 500, withError = false)
    }

    private fun doCreate(context: Context, uris: List<IdWithBitmap>, parentFolder: String, itemId: String) : Boolean {
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
        bitmap.recycle()
        out.close()
    }

}

data class IdWithBitmap(
        val id: Long,
        val bitmap: Bitmap
)