package dev.olog.presentation.widgets.imageview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import dev.olog.presentation.R
import dev.olog.shared.extensions.dpToPx
import kotlin.properties.Delegates

class ShadowImageView @JvmOverloads constructor(
        context: Context,
        attr: AttributeSet? = null

) : AppCompatImageView(context, attr) {

    companion object {
        private const val DEFAULT_RADIUS = 0.5f
        private const val DEFAULT_COLOR = -1
        private const val BRIGHTNESS = -25f
        private const val SATURATION = 1.3f
        private const val TOP_OFFSET = 2.2f
        private const val PADDING = 22f
    }

    var radiusOffset by Delegates.vetoable(DEFAULT_RADIUS) { _, _, newValue ->
        newValue > 0F || newValue <= 1
    }

    var shadowColor =
        DEFAULT_COLOR

    init {
        if (!isInEditMode){
            BlurShadow.init(context.applicationContext)
            cropToPadding = false
            super.setScaleType(ScaleType.CENTER_CROP)
            val padding = context.dpToPx(PADDING)
            setPadding(padding, padding, padding, padding)
            val typedArray = context.obtainStyledAttributes(attr, R.styleable.ShadowView, 0, 0)
            shadowColor = typedArray.getColor(R.styleable.ShadowView_shadowColor,
                DEFAULT_COLOR
            )
            radiusOffset = typedArray.getFloat(R.styleable.ShadowView_radiusOffset,
                DEFAULT_RADIUS
            )
            typedArray.recycle()
        }
    }

    override fun setImageBitmap(bm: Bitmap?) {
        if (!isInEditMode){
            setBlurShadow { super.setImageDrawable(BitmapDrawable(resources, bm)) }
        } else {
            super.setImageBitmap(bm)
        }
    }

    override fun setImageResource(resId: Int) {
        if (!isInEditMode){
            setBlurShadow { super.setImageDrawable(ContextCompat.getDrawable(context, resId)) }
        } else {
            super.setImageResource(resId)
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        if (!isInEditMode){
            setBlurShadow { super.setImageDrawable(drawable) }
        } else {
            super.setImageDrawable(drawable)
        }
    }

    override fun setScaleType(scaleType: ScaleType?) {
        super.setScaleType(ScaleType.CENTER_CROP)
    }

    private inline fun setBlurShadow(crossinline setImage: () -> Unit = {}) {
        if (width <= 0 || height <= 0){
            doOnPreDraw {
                background = null
                setImage()
                makeBlurShadow()
            }
        } else {
            background = null
            setImage()
            makeBlurShadow()
        }
    }

    private fun makeBlurShadow() {
        var radius = resources.getInteger(R.integer.radius).toFloat()
        radius *= 2 * radiusOffset
        val blur = BlurShadow.blur(this, width, height - context.dpToPx(TOP_OFFSET), radius)
        //brightness -255..255 -25 is default
        val colorMatrix = ColorMatrix(floatArrayOf(
                1f, 0f, 0f, 0f,
            BRIGHTNESS,
                0f, 1f, 0f, 0f,
            BRIGHTNESS,
                0f, 0f, 1f, 0f,
            BRIGHTNESS,
                0f, 0f, 0f, 1f, 0f)).apply { setSaturation(SATURATION) }

        background = BitmapDrawable(resources, blur).apply {
            this.colorFilter = ColorMatrixColorFilter(colorMatrix)
            applyShadowColor(this)
        }
        //super.setImageDrawable(null)
    }

    private fun applyShadowColor(bitmapDrawable: BitmapDrawable) {
        if (shadowColor != DEFAULT_COLOR) {
            bitmapDrawable.colorFilter = PorterDuffColorFilter(shadowColor, PorterDuff.Mode.SRC_IN)
        }
    }

}