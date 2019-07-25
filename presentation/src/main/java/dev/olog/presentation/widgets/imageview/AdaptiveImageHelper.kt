package dev.olog.presentation.widgets.imageview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import dev.olog.shared.android.extensions.layers
import dev.olog.shared.android.extensions.lazyFast
import dev.olog.shared.android.widgets.adaptive.AdaptiveColorImageViewPresenter

internal class AdaptiveImageHelper(context: Context) {

    private val presenter by lazyFast {
        AdaptiveColorImageViewPresenter(
            context
        )
    }

    fun setImageBitmap(bm: Bitmap?) {
        presenter.onNextImage(bm)
    }

    fun setImageDrawable(drawable: Drawable?) {
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