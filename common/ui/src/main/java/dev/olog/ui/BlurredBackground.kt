package dev.olog.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import dev.olog.core.MediaId
import dev.olog.shared.extension.isDarkMode
import dev.olog.shared.extension.lazyFast
import io.alterac.blurkit.BlurKit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

class BlurredBackground(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageView(context, attrs) {

    private var job: Job? = null

    companion object {
        private const val LIGHT_MODE_SIZE = 250
        private const val DARK_MODE_SIZE = 175
        private const val BLUR_RADIUS = 25
    }

    init {
        scaleType = ScaleType.CENTER_CROP
        adjustViewBounds = true
    }

    private val isDarkMode by lazyFast { context.isDarkMode() }

    fun loadImage(mediaId: MediaId, drawable: Drawable?) {
        if (drawable == null){
            return
        }
        job?.cancel()
        job = GlobalScope.launch(Dispatchers.IO) {
            try {
                loadImageInternal(mediaId, drawable.mutate())
            } catch (ex: Throwable){
                ex.printStackTrace()
            }
        }

    }

    private suspend fun loadImageInternal(mediaId: MediaId, drawable: Drawable) {

        val size = if (isDarkMode) DARK_MODE_SIZE else LIGHT_MODE_SIZE

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

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        job?.cancel()
    }

}