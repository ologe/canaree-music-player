package dev.olog.ui.adaptive

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import dev.olog.shared.extension.lazyFast

class AdaptiveImageHelper(context: Context) {

    private val presenter by lazyFast {
        AdaptiveColorImageViewPresenter(
            context
        )
    }

    fun setImageBitmap(bm: Bitmap?) {
        presenter.onNextImage(bm)
    }

    fun setImageDrawable(drawable: Drawable?) {
        presenter.onNextImage(drawable)
    }

    fun observeProcessorColors() = presenter.observeProcessorColors()
    fun observePaletteColors() = presenter.observePalette()

}