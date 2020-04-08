package dev.olog.presentation.widgets.imageview.blurshadow

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import dev.olog.feature.presentation.base.extensions.dpToPx
import dev.olog.shared.coroutines.autoDisposeJob
import dev.olog.shared.android.extensions.launchWhenResumed
import kotlinx.coroutines.android.awaitFrame

class BlurShadowHelper(
    private val view: AppCompatImageView
) {

    companion object {
        private const val BRIGHTNESS = -25f
        private const val SATURATION = 1.3f
        private const val TOP_OFFSET = 2.2f
        private const val PADDING = 22f
    }

    private var job by autoDisposeJob()

    init {
        BlurShadow.init(view.context.applicationContext)
        view.apply {
            cropToPadding = false
            scaleType = ImageView.ScaleType.CENTER_CROP

            val padding = context.dpToPx(PADDING)
            setPadding(padding, padding, padding, padding)
        }
    }

    fun setBlurShadow() {
        view.background = null
        tryMakeBlurShadow()
    }

    private fun tryMakeBlurShadow() {
        job = view.launchWhenResumed {
            loopUntilSizeIsValid()
        }
    }

    private suspend fun loopUntilSizeIsValid() {
        while (!(view.width > 0 || view.height > 0)) {
            awaitFrame()
        }
        makeBlurShadow()
    }

    private fun makeBlurShadow() {
        val blur = BlurShadow.blur(
            view,
            view.width,
            view.height - view.context.dpToPx(TOP_OFFSET)
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