package dev.olog.feature.player.player.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import dev.olog.domain.mediaid.MediaId
import dev.olog.lib.image.provider.CoverUtils
import dev.olog.shared.android.coroutine.viewScope
import dev.olog.shared.android.extensions.isDarkMode
import dev.olog.shared.autoDisposeJob
import io.alterac.blurkit.BlurKit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

class BlurredBackground(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageView(context, attrs) {

    private var job by autoDisposeJob()

    companion object {
        private const val LIGHT_MODE_SIZE = 250
        private const val DARK_MODE_SIZE = 175
        private const val BLUR_RADIUS = 25
    }

    init {
        scaleType = ScaleType.CENTER_CROP
        adjustViewBounds = true
    }

    fun loadImage(mediaId: MediaId, drawable: Drawable?) {
        if (drawable == null) {
            job = null
            return
        }
        job = viewScope.launch {
            try {
                loadImageInternal(mediaId, drawable.mutate())
            } catch (ex: Throwable){
                ex.printStackTrace()
            }
        }

    }

    private suspend fun loadImageInternal(
        mediaId: MediaId,
        drawable: Drawable
    ) = withContext(Dispatchers.IO) {

        val size = if (context.isDarkMode) DARK_MODE_SIZE else LIGHT_MODE_SIZE

        val bitmap = if (drawable is LayerDrawable){
            CoverUtils.onlyGradient(context, mediaId).toBitmap(size, size)
        } else {
            drawable.toBitmap(size, size)
        }
        yield()
        val blurred = BlurKit.getInstance().blur(bitmap, BLUR_RADIUS).toDrawable(resources)
        yield()
        withContext(Dispatchers.Main) {
            background = blurred
        }
    }

}