package dev.olog.msc.presentation.utils.images

import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.widget.ImageView
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import dev.olog.msc.R
import dev.olog.msc.presentation.widget.ForegroundImageView
import dev.olog.msc.utils.ViewUtils
import dev.olog.msc.utils.k.extension.getBitmap

class RippleTarget(
        private val imageView: ImageView,
        private val bounded: Boolean

) : DrawableImageViewTarget(imageView), Palette.PaletteAsyncListener {

    override fun onResourceReady(drawable: Drawable, transition: Transition<in Drawable>?) {
        super.onResourceReady(drawable, transition)
        if (imageView is ForegroundImageView){
            val bitmap = drawable.getBitmap() ?: return
            Palette.from(bitmap).clearFilters().generate(this)
        }
    }

    override fun onGenerated(palette: Palette) {
        if (imageView is ForegroundImageView){
            imageView.foreground = ViewUtils.createRipple(palette, .5f, .5f,
                    ContextCompat.getColor(view.context, R.color.mid_grey), bounded)
        }
    }
}