package dev.olog.image.provider.merger

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.graphics.drawable.toDrawable
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.ImageRetrieverResult
import dev.olog.image.provider.loading.ImageSize
import dev.olog.image.provider.loading.LoadErrorStrategy
import dev.olog.image.provider.loading.loadImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.inject.Inject

internal class ImageMergerFactory @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageMerger: ImageMerger,
) {

    /**
     * @return non null -> image created
     *         null -> not enough images to generate an image
     */
    suspend fun create(albumIds: List<Long>): ImageRetrieverResult<InputStream> {
        val albumsId = albumIds.distinctBy { it }
        val drawables = mutableListOf<Drawable>()
        for (id in albumsId) {
            val drawable = getDrawable(context, id) ?: continue
            drawables.add(drawable)
            if (drawables.size == ImageMerger.PARTS) {
                break
            }
        }
        if (drawables.isEmpty()) {
            return ImageRetrieverResult.NotFound
        }

        val bitmap = imageMerger.execute(drawables)
        try {
            return ImageRetrieverResult.Success(bitmap.toInputStream())
        } finally {
            bitmap.recycle()
        }
    }

    private fun Bitmap.toInputStream(): InputStream {
        return ByteArrayOutputStream().use { out ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, out)
            } else {
                @Suppress("DEPRECATION")
                compress(Bitmap.CompressFormat.WEBP, 90, out)
            }
            ByteArrayInputStream(out.toByteArray())
        }
    }

    private suspend fun getDrawable(context: Context, albumId: Long): Drawable? {
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.ALBUMS, albumId.toString())
        return context.loadImage(
            mediaId = mediaId,
            loadError = LoadErrorStrategy.None,
            imageSize = ImageSize.Large,
        )?.toDrawable(context.resources) // todo check
    }

}