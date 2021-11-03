package dev.olog.presentation.widgets.imageview.blurshadow

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import dev.olog.shared.android.extensions.dip
import kotlinx.coroutines.*

class BlurShadowHelper(
    private val view: AppCompatImageView
) : CoroutineScope by MainScope() {

    companion object {
        private const val BRIGHTNESS = -25f
        private const val SATURATION = 1.3f
        private const val TOP_OFFSET = 2.2f
        private const val PADDING = 22f
    }

    private var job: Job? = null

    init {
        BlurShadow.init(view.context.applicationContext)
        view.apply {
            cropToPadding = false
            scaleType = ImageView.ScaleType.CENTER_CROP

            val padding = context.dip(PADDING)
            setPadding(padding, padding, padding, padding)
        }
    }

    fun setBlurShadow() {
        view.background = null
        tryMakeBlurShadow()
    }

    private fun tryMakeBlurShadow() {
        job?.cancel()
        job = launch {
            loopUntilSizeIsValid()
        }
    }

    private suspend fun loopUntilSizeIsValid() {
        while (!(view.width > 0 || view.height > 0)) {
            delay(16) // wait a frame
        }
        makeBlurShadow()
    }

    private fun makeBlurShadow() {
        val blur = BlurShadow.blur(
            view,
            view.width,
            view.height - view.context.dip(TOP_OFFSET)
        )
        //brightness -255..255 -25 is default
        val colorMatrix = ColorMatrix(
            floatArrayOf(
                1f, 0f, 0f, 0f,
                BRIGHTNESS,
                0f, 1f, 0f, 0f,
                BRIGHTNESS,
                0f, 0f, 1f, 0f,
                BRIGHTNESS,
                0f, 0f, 0f, 1f, 0f
            )
        ).apply { setSaturation(SATURATION) }

        view.background = BitmapDrawable(view.resources, blur).apply {
            this.colorFilter = ColorMatrixColorFilter(colorMatrix)
        }
    }

}