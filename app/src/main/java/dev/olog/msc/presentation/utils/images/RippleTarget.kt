package dev.olog.msc.presentation.utils.images

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import dev.olog.msc.R
import dev.olog.msc.presentation.widget.image.view.ForegroundImageView
import dev.olog.msc.presentation.widget.parallax.ParallaxImageView
import dev.olog.msc.utils.RippleUtils
import dev.olog.msc.utils.k.extension.getBitmap
import java.lang.ref.WeakReference

class RippleTarget(
        view: ImageView,
        private val isLeaf : Boolean

) : DrawableImageViewTarget(view), androidx.palette.graphics.Palette.PaletteAsyncListener {

    private val imageView = WeakReference(view)

    init {
        if (isLeaf && view is ForegroundImageView){
            view.foreground = null
        }
    }

    override fun onResourceReady(drawable: Drawable, transition: Transition<in Drawable>?) {
        super.onResourceReady(drawable, transition)

        if (!isLeaf && imageView.get() is ForegroundImageView){
            val bitmap = drawable.getBitmap() ?: return
            androidx.palette.graphics.Palette.from(bitmap).clearFilters().generate(this)
        }
    }

    @SuppressLint("NewApi")
    override fun onGenerated(palette: androidx.palette.graphics.Palette?) {
        val view = imageView.get() ?: return

        if (!isLeaf && view is ForegroundImageView){
            val fallbackColor = ContextCompat.getColor(view.context, R.color.mid_grey)
            val darkAlpha = .1f
            val lightAlpha = .2f

            view.foreground = RippleUtils.create(palette, darkAlpha,
                    lightAlpha, fallbackColor, true)

            if (view is ParallaxImageView){
                view.setScrimColor(RippleUtils.createColor(palette, darkAlpha,
                        lightAlpha, fallbackColor))
            }
        }
    }
}