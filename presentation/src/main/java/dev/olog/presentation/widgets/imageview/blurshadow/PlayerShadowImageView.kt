package dev.olog.presentation.widgets.imageview.blurshadow

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import dev.olog.presentation.widgets.imageview.shape.ShapeImageView
import dev.olog.shared.lazyFast

class PlayerShadowImageView(
    context: Context,
    attr: AttributeSet

) : ShapeImageView(context, attr) {

    private val helper by lazyFast { BlurShadowHelper(this) }

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

