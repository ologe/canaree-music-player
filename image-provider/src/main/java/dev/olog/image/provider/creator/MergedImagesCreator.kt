package dev.olog.image.provider.creator

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.util.Util.assertBackgroundThread
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.image.provider.OnImageLoadingError
import dev.olog.image.provider.getCachedBitmap
import kotlinx.coroutines.yield
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

internal object MergedImagesCreator {

    suspend fun makeImages(
        context: Context,
        albumIdList: List<Long>,
        parentFolder: String,
        itemId: String
    ): File? {
        assertBackgroundThread()

        val albumsId = albumIdList.distinctBy { it }
        val uris = mutableListOf<IdWithBitmap>()
        for (id in albumsId) {
            try {
                getBitmap(context, id)?.let { uris.add(IdWithBitmap(id, it)) }
            } catch (ex: Exception) {
                Timber.e(ex)
            }
            if (uris.size == 9) {
                break
            }
        }
        yield()

        try {
            return doCreate(
                context,
                uris,
                parentFolder,
                itemId
            )
        } catch (ex: OutOfMemoryError) {
            Timber.e(ex)
            // Romain Guy says it's bad
            return null
        }
    }

    private suspend fun getBitmap(context: Context, albumId: Long): Bitmap? {
        val mediaId = MediaId.Category(MediaIdCategory.ALBUMS, albumId)
        val bitmap = context.getCachedBitmap(mediaId, 500, onError = OnImageLoadingError.None)
        yield()
        return bitmap
    }

    private suspend fun doCreate(
        context: Context,
        uris: List<IdWithBitmap>,
        parentFolder: String,
        itemId: String
    ): File? {
        val imageDirectory = ImagesFolderUtils.getImageFolderFor(context, parentFolder)

        if (uris.isEmpty()) {
            // new requested image has no childs, delete old if exists
            imageDirectory.listFiles()
                ?.firstOrNull { it.name.substring(0, it.name.indexOf("_")) == itemId }
                ?.delete()
            return null
        }

        val albumsId = uris.map { it.id }

        // search for old image
        val oldImage = imageDirectory.listFiles()
            ?.firstOrNull { it.name.substring(0, it.name.indexOf("_")) == itemId }

        if (oldImage != null) { // image found
            val fileImageName = oldImage.extractImageName()

            val albumIdsInFilename = fileImageName.albums

            val sameImages = albumsId.sorted() == albumIdsInFilename.sorted()
            if (sameImages) {
                // same image, exit
                return oldImage
            } else {
                // images are different
                val progr = fileImageName.progressive

                // image already exist, create new with a new progr
                oldImage.delete() // first delete old
                return prepareSaveThenSave(
                    uris,
                    imageDirectory,
                    itemId,
                    albumsId,
                    progr + 1L
                )
            }
        } else {
            // create new image
            return prepareSaveThenSave(
                uris,
                imageDirectory,
                itemId,
                albumsId,
                System.currentTimeMillis()
            )
        }
    }

    private suspend fun prepareSaveThenSave(
        uris: List<IdWithBitmap>, directory: File,
        itemId: String,
        albumsId: List<Long>,
        progr: Long
    ): File {
        yield()
        val bitmap = MergedImageUtils.joinImages(uris.map { it.bitmap })
        val child = ImagesFolderUtils.createFileName(itemId, progr, albumsId)
        return saveFile(directory, child, bitmap)
    }

    private fun saveFile(directory: File, child: String, bitmap: Bitmap): File {
        assertBackgroundThread()

        val dest = File(directory, "$child.webp")
        val out = FileOutputStream(dest)
        bitmap.compress(Bitmap.CompressFormat.WEBP, 90, out)
        bitmap.recycle()
        out.close()
        return dest
    }

}

data class IdWithBitmap(
    val id: Long,
    val bitmap: Bitmap
)