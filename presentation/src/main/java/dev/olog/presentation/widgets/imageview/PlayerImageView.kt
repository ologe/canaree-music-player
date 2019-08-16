package dev.olog.presentation.widgets.imageview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.bumptech.glide.Priority
import dev.olog.core.MediaId
import dev.olog.core.gateway.getImageVersionGateway
import dev.olog.image.provider.CoverUtils
import dev.olog.image.provider.CustomMediaStoreSignature
import dev.olog.image.provider.GlideApp
import dev.olog.presentation.ripple.RippleTarget
import dev.olog.presentation.widgets.imageview.shape.ShapeImageView
import dev.olog.shared.lazyFast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class PlayerImageView (
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

    open fun loadImage(mediaId: MediaId) {
        GlideApp.with(context).clear(this)

        GlobalScope.launch(Dispatchers.Main) {
            val version = withContext(Dispatchers.Default){
                context.getImageVersionGateway().getCurrentVersion(mediaId)
            }

            GlideApp.with(context)
                .load(mediaId)
                .placeholder(CoverUtils.getGradient(context, mediaId))
                .priority(Priority.IMMEDIATE)
                .override(500)
                .onlyRetrieveFromCache(true)
                .signature(CustomMediaStoreSignature(mediaId, version))
                .into(RippleTarget(this@PlayerImageView))
        }
    }

}

