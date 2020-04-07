package dev.olog.shared.widgets.adaptive

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import dev.olog.shared.lazyFast

class AdaptiveColorImageView(
    context: Context,
    attr: AttributeSet

) : AppCompatImageView(context, attr) {

    private val presenter by lazyFast {
        AdaptiveColorImageViewPresenter(this, context)
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        if (!isInEditMode) {
            presenter.onNextImage(bm)
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (isInEditMode) {
            return
        }

        presenter.onNextImage(drawable)
    }

    fun observeProcessorColors() = presenter.observeProcessorColors()
    fun observePaletteColors() = presenter.observePalette()

}