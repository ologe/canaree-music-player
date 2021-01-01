package dev.olog.feature.player.player.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.bumptech.glide.Priority
import dev.olog.core.mediaid.MediaId
import dev.olog.lib.image.provider.CoverUtils
import dev.olog.lib.image.provider.GlideApp
import dev.olog.lib.image.provider.target.RippleTarget
import dev.olog.shared.widgets.imageview.ShapeImageView
import dev.olog.shared.lazyFast

class PlayerImageView (
    context: Context,
    attr: AttributeSet
) : ShapeImageView(context, attr) {

    private val adaptiveImageHelper by lazyFast {
        AdaptiveImageHelper(context)
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        if (!isInEditMode) {
            adaptiveImageHelper.setImageBitmap(bm)
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (!isInEditMode) {
            adaptiveImageHelper.setImageDrawable(drawable)
        }
    }

    fun observeProcessorColors() = adaptiveImageHelper.observeProcessorColors()
    fun observePaletteColors() = adaptiveImageHelper.observePaletteColors()

    fun loadImage(mediaId: MediaId) {
        GlideApp.with(context).clear(this)

        GlideApp.with(context)
            .load(mediaId)
            .error(CoverUtils.getGradient(context, mediaId))
            .priority(Priority.IMMEDIATE)
            .override(500)
            .onlyRetrieveFromCache(true)
            .into(RippleTarget(this@PlayerImageView))
    }

}

