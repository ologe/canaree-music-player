package dev.olog.presentation.widgets

import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import dev.olog.core.MediaId
import dev.olog.image.provider.CoverUtils
import dev.olog.image.provider.getCachedDrawable
import dev.olog.shared.android.utils.assertBackgroundThread
import io.alterac.blurkit.BlurKit
import kotlinx.coroutines.*

class BlurredBackground(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageView(context, attrs) {

    private var job: Job? = null

    fun loadImage(mediaId: MediaId) {
        job?.cancel()
        job = GlobalScope.launch(Dispatchers.IO) {
            try {
                loadImageInternal(mediaId)
            } catch (ex: Throwable){
                ex.printStackTrace()
            }
        }
    }

    private suspend fun loadImageInternal(mediaId: MediaId) {

        assertBackgroundThread()

        val drawable = context.getCachedDrawable(mediaId, 100) ?: return
        val bitmap = if (drawable is LayerDrawable){
            CoverUtils.onlyGradient(context, mediaId).toBitmap(100, 100)
        } else {
            drawable.toBitmap()
        }
        yield()
        val blurred = BlurKit.getInstance().blur(bitmap, 25).toDrawable(resources)
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