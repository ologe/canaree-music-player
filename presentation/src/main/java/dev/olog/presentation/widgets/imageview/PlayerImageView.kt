package dev.olog.presentation.widgets.imageview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.util.AttributeSet
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.Target
import dev.olog.core.MediaId
import dev.olog.image.provider.CoverUtils
import dev.olog.image.provider.GlideApp
import dev.olog.presentation.ripple.RippleTarget
import dev.olog.shared.extensions.layers
import dev.olog.shared.extensions.lazyFast
import dev.olog.shared.widgets.adaptive.AdaptiveColorImageView
import dev.olog.shared.widgets.adaptive.AdaptiveColorImageViewPresenter

open class PlayerImageView @JvmOverloads constructor(
        context: Context,
        attr: AttributeSet? = null

) : ShapeImageView(context, attr) {

    private val adaptiveImageHelper by lazyFast { AdaptiveImageHelper(context) }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        if (!isInEditMode){
            adaptiveImageHelper.setImageBitmap(bm)
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (!isInEditMode){
            adaptiveImageHelper.setImageDrawable(drawable)
        }
    }

    fun observeProcessorColors() = adaptiveImageHelper.observeProcessorColors()
    fun observePaletteColors() = adaptiveImageHelper.observePaletteColors()

    open fun loadImage(mediaId: MediaId){

        GlideApp.with(context).clear(this)
        GlideApp.with(context)
                .load(mediaId)
                .placeholder(CoverUtils.getGradient(context, mediaId))
                .priority(Priority.IMMEDIATE)
                .transition(DrawableTransitionOptions.withCrossFade())
                .override(Target.SIZE_ORIGINAL)
                .into(RippleTarget(this))
    }

}

