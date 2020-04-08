package dev.olog.presentation.ripple

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import dev.olog.core.coroutines.viewScope
import dev.olog.presentation.widgets.parallax.ParallaxImageView
import dev.olog.shared.coroutines.autoDisposeJob
import dev.olog.feature.presentation.base.widget.ForegroundImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RippleTarget(
    imageView: ImageView,
    private val fallbackColor: Int = 0x40_606060,
    private val darkAlpha: Float = .1f,
    private val lightAlpha: Float = .2f

) : DrawableImageViewTarget(imageView) {

    private var job by autoDisposeJob()

    @SuppressLint("ConcreteDispatcherIssue")
    override fun onResourceReady(drawable: Drawable, transition: Transition<in Drawable>?) {
        super.onResourceReady(drawable, transition)
        if (view is ForegroundImageView) {
            job = view.viewScope.launchWhenAttached(Dispatchers.Default) {
                generateRipple(drawable)
            }
        }
    }

    private suspend fun generateRipple(drawable: Drawable) {
        val bitmap = drawable.toBitmap()
        yield()
        val palette = generatePalette(bitmap)
        yield()
        onGenerated(palette)
    }

    private suspend fun generatePalette(bitmap: Bitmap) =
        suspendCoroutine<Palette?> { continuation ->
            Palette.from(bitmap).clearFilters().generate {
                continuation.resume(it)
            }
        }

    @SuppressLint("ConcreteDispatcherIssue")
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