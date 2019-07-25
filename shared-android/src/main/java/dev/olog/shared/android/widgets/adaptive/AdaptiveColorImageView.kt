package dev.olog.shared.android.widgets.adaptive

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.util.AttributeSet
import dev.olog.shared.android.extensions.layers
import dev.olog.shared.lazyFast
import dev.olog.shared.android.widgets.ForegroundImageView

open class AdaptiveColorImageView @JvmOverloads constructor(
        context: Context,
        attr: AttributeSet? = null

) : ForegroundImageView(context, attr) {

    private val presenter by dev.olog.shared.lazyFast {
        AdaptiveColorImageViewPresenter(
            context
        )
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        if (!isInEditMode){
            presenter.onNextImage(bm)
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (isInEditMode){
            return
        }

        if (drawable is TransitionDrawable){
            if (drawable.numberOfLayers == 2){
                presenter.onNextImage(drawable.layers[1])
            } else {
                presenter.onNextImage(drawable)
            }

        } else {
            presenter.onNextImage(drawable)
        }
    }

    fun observeProcessorColors() = presenter.observeProcessorColors()
    fun observePaletteColors() = presenter.observePalette()

}