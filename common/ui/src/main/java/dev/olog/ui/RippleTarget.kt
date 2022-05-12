package dev.olog.ui

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import dev.olog.shared.extension.coroutineScope
import dev.olog.ui.ForegroundImageView
import dev.olog.ui.RippleUtils
import dev.olog.ui.parallax.ParallaxImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

class RippleTarget(
    imageView: ImageView,
    private val fallbackColor: Int = 0x40_606060,
    private val darkAlpha: Float = .1f,
    private val lightAlpha: Float = .2f

) : DrawableImageViewTarget(imageView) {

    override fun onResourceReady(drawable: Drawable, transition: Transition<in Drawable>?) {
        super.onResourceReady(drawable, transition)
        if (view.isAttachedToWindow && view is ForegroundImageView) {
            view.coroutineScope.launch {
                generateRipple(drawable)
            }
        }
    }

    private suspend fun generateRipple(drawable: Drawable) = with(Dispatchers.Default) {
        val bitmap = drawable.toBitmap()
        yield()
        val palette = generatePalette(bitmap)
        yield()
        onGenerated(palette)
    }

    private fun generatePalette(bitmap: Bitmap): Palette {
        return Palette.from(bitmap).clearFilters().generate()
    }

    private suspend fun onGenerated(palette: Palette?) = withContext(Dispatchers.Main) {
        if (view is ForegroundImageView) {

            view.foreground = RippleUtils.create(
                palette, darkAlpha,
                lightAlpha, fallbackColor, true
            )
        }
        if (view is ParallaxImageView) {
            view.setScrimColor(
                RippleUtils.createColor(
                    palette,
                    darkAlpha,
                    lightAlpha,
                    fallbackColor
                )
            )
        }
    }
}