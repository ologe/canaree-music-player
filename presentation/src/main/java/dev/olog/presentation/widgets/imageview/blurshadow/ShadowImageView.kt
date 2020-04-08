package dev.olog.presentation.widgets.imageview.blurshadow

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import dev.olog.feature.presentation.base.blur.BlurShadowHelper
import dev.olog.shared.lazyFast

class ShadowImageView(
    context: Context,
    attr: AttributeSet

) : AppCompatImageView(context, attr) {

    private val helper by lazyFast {
        BlurShadowHelper(
            this
        )
    }

    init {
        scaleType = ScaleType.CENTER_CROP
        adjustViewBounds = true
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        if (!isInEditMode) {
            helper.setBlurShadow()
        }
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        if (!isInEditMode) {
            helper.setBlurShadow()
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)

        if (!isInEditMode) {
            helper.setBlurShadow()
        }
    }

    override fun setScaleType(scaleType: ScaleType?) {
        super.setScaleType(ScaleType.CENTER_CROP)
    }


}