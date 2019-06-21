package dev.olog.msc.presentation.widget.image.view.player

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.media.MediaMetadataCompat
import android.util.AttributeSet
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import dev.olog.msc.app.GlideApp
import dev.olog.presentation.model.DisplayableItem
import dev.olog.msc.presentation.widget.image.view.ForegroundImageView
import dev.olog.msc.utils.RippleUtils
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.k.extension.getBitmap
import dev.olog.msc.utils.k.extension.getImage
import dev.olog.msc.utils.k.extension.getMediaId
import dev.olog.shared.isMarshmallow

open class PlayerImageView @JvmOverloads constructor(
        context: Context,
        attr: AttributeSet? = null

) : AdaptiveColorImageView(context, attr) {


    open fun loadImage(metadata: MediaMetadataCompat){
        val mediaId = metadata.getMediaId()

        val model = metadata.toPlayerImage()

        GlideApp.with(context).clear(this)

        GlideApp.with(context)
                .load(model)
                .placeholder(CoverUtils.getGradient(context, mediaId))
                .priority(Priority.IMMEDIATE)
                .transition(DrawableTransitionOptions.withCrossFade())
                .override(Target.SIZE_ORIGINAL)
                .into(Ripple(this))
    }

    class Ripple(private val imageView: ForegroundImageView) : DrawableImageViewTarget(imageView), androidx.palette.graphics.Palette.PaletteAsyncListener {

        override fun onResourceReady(drawable: Drawable, transition: Transition<in Drawable>?) {
            super.onResourceReady(drawable, transition)
            if (isMarshmallow()){
                val bitmap = drawable.getBitmap() ?: return
                androidx.palette.graphics.Palette.from(bitmap).clearFilters().generate(this)
            }
        }

        override fun onGenerated(palette: androidx.palette.graphics.Palette?) {
            val fallbackColor = 0x40606060
            val darkAlpha = .3f
            val lightAlpha = .3f

            imageView.foreground = RippleUtils.create(palette, darkAlpha,
                    lightAlpha, fallbackColor, true)
        }

    }

}


fun MediaMetadataCompat.toPlayerImage(): DisplayableItem {
    // only mediaId and image is needed
    return DisplayableItem(0, this.getMediaId(), "", image = this.getImage())
}

