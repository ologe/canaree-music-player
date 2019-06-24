package dev.olog.msc.presentation.widget.image.view.player

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.util.AttributeSet
import dev.olog.shared.extensions.lazyFast
import dev.olog.shared.widgets.ForegroundImageView
import dev.olog.msc.utils.k.extension.layers

open class AdaptiveColorImageView @JvmOverloads constructor(
        context: Context,
        attr: AttributeSet? = null

) : ForegroundImageView(context, attr) {

    private val presenter by lazyFast { AdaptiveColorImageViewPresenter(context) }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        presenter.onNextImage(bm)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
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