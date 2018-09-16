package dev.olog.msc.presentation.utils.images

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.widget.ImageView
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import dev.olog.msc.R
import dev.olog.msc.presentation.widget.image.view.ForegroundImageView
import dev.olog.msc.presentation.widget.parallax.ParallaxImageView
import dev.olog.msc.utils.RippleUtils
import dev.olog.msc.utils.isMarshmallow
import dev.olog.msc.utils.k.extension.getBitmap
import java.lang.ref.WeakReference

class RippleTarget(
        view: ImageView,
        private val isLeaf : Boolean

) : DrawableImageViewTarget(view), Palette.PaletteAsyncListener {

    private val imageView = WeakReference(view)

    init {
        if (isLeaf && imageView is ForegroundImageView){
            imageView.foreground = null
        }
    }

    override fun onResourceReady(drawable: Drawable, transition: Transition<in Drawable>?) {
        super.onResourceReady(drawable, transition)

        if (!isLeaf && imageView.get() is ForegroundImageView){
            val bitmap = drawable.getBitmap() ?: return
            Palette.from(bitmap).clearFilters().generate(this)
        }
    }

    @SuppressLint("NewApi")
    override fun onGenerated(palette: Palette?) {
        val view = imageView.get() ?: return

        if (!isLeaf && view is ForegroundImageView){
            val fallbackColor = ContextCompat.getColor(view.context, R.color.mid_grey)
            val darkAlpha = if (isMarshmallow()) .4f else .1f
            val lightAlpha = if (isMarshmallow()) .5f else .2f

            view.foreground = RippleUtils.create(palette, darkAlpha,
                    lightAlpha, fallbackColor, true)

            if (view is ParallaxImageView){
                view.setScrimColor(RippleUtils.createColor(palette, darkAlpha,
                        lightAlpha, fallbackColor))
            }
        }
    }
}