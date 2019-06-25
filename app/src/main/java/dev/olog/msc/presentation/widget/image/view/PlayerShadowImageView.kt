package dev.olog.msc.presentation.widget.image.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.support.v4.media.MediaMetadataCompat
import android.util.AttributeSet
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Priority
import com.bumptech.glide.request.target.Target
import dev.olog.image.provider.CoverUtils
import dev.olog.image.provider.GlideApp
import dev.olog.media.getMediaId
import dev.olog.msc.R
import dev.olog.msc.presentation.widget.image.view.PlayerShadowImageView.Companion.DOWNSCALE_FACTOR
import dev.olog.presentation.widgets.PlayerImageView
import dev.olog.presentation.widgets.toPlayerImage
import dev.olog.shared.extensions.dpToPx
import dev.olog.presentation.ripple.RippleTarget
import kotlin.properties.Delegates

class PlayerShadowImageView @JvmOverloads constructor(
        context: Context,
        attr: AttributeSet? = null

) : PlayerImageView(context, attr) {

    companion object {
        private const val DEFAULT_RADIUS = 0.5f
        private const val DEFAULT_COLOR = -1
        private const val BRIGHTNESS = -25f
        private const val SATURATION = 1.3f
        private const val TOP_OFFSET = 2.2f
        private const val PADDING = 22f
        internal const val DOWNSCALE_FACTOR = 0.2f
    }

    var radiusOffset by Delegates.vetoable(DEFAULT_RADIUS) { _, _, newValue ->
        newValue > 0F || newValue <= 1
    }

    var shadowColor = DEFAULT_COLOR

    init {
        if (!isInEditMode){
            BlurShadow.init(context.applicationContext)
            cropToPadding = false
            super.setScaleType(ScaleType.CENTER_CROP)
            val padding = context.dpToPx(PADDING)
            setPadding(padding, padding, padding, padding)
            val typedArray = context.obtainStyledAttributes(attr, R.styleable.ShadowView, 0, 0)
            shadowColor = typedArray.getColor(R.styleable.ShadowView_shadowColor, DEFAULT_COLOR)
            radiusOffset = typedArray.getFloat(R.styleable.ShadowView_radiusOffset, DEFAULT_RADIUS)
            typedArray.recycle()
        }
    }

    override fun loadImage(metadata: MediaMetadataCompat){
        val mediaId = metadata.getMediaId()

        val model = metadata.toPlayerImage()

        GlideApp.with(context).clear(this)

        GlideApp.with(context)
                .load(model)
                .placeholder(CoverUtils.getGradient(context, mediaId))
                .priority(Priority.IMMEDIATE)
                .override(Target.SIZE_ORIGINAL)
                .into(RippleTarget(this))
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

    fun setImageResource(resId: Int, withShadow: Boolean) {
        if (withShadow) {
            setImageResource(resId)
        } else {
            background = null
            super.setImageResource(resId)
        }
    }

    fun setImageDrawable(drawable: Drawable?, withShadow: Boolean) {
        if (withShadow) {
            setImageDrawable(drawable)
        } else {
            background = null
            super.setImageDrawable(drawable)
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

    private fun setBlurShadow(setImage: () -> Unit = {}) {
        background = null
        if (height != 0 || measuredHeight != 0) {
            setImage()
            makeBlurShadow()
        } else {
            val preDrawListener = object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    viewTreeObserver.removeOnPreDrawListener(this)
                    setImage()
                    makeBlurShadow()
                    return false
                }
            }
            viewTreeObserver.addOnPreDrawListener(preDrawListener)
        }
    }

    private fun makeBlurShadow() {
        var radius = resources.getInteger(R.integer.radius).toFloat()
        radius *= 2 * radiusOffset
        val blur = BlurShadow.blur(this, width, height - context.dpToPx(TOP_OFFSET), radius)
        //brightness -255..255 -25 is default
        val colorMatrix = ColorMatrix(floatArrayOf(
                1f, 0f, 0f, 0f, BRIGHTNESS,
                0f, 1f, 0f, 0f, BRIGHTNESS,
                0f, 0f, 1f, 0f, BRIGHTNESS,
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

object BlurShadow {

    private var renderScript: RenderScript? = null

    fun init(context: Context) {
        if (renderScript == null)
            renderScript = RenderScript.create(context)
    }

    fun blur(view: ImageView, width: Int, height: Int, radius: Float): Bitmap? {
        val src = getBitmapForView(view, DOWNSCALE_FACTOR, width, height) ?: return null
        val input = Allocation.createFromBitmap(renderScript, src)
        val output = Allocation.createTyped(renderScript, input.type)
        val script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        script.apply {
            setRadius(radius)
            setInput(input)
            forEach(output)
        }
        output.copyTo(src)
        return src
    }

    private fun getBitmapForView(view: ImageView, downscaleFactor: Float, width: Int, height: Int): Bitmap? {
        val bitmap = Bitmap.createBitmap(
                (width * downscaleFactor).toInt(),
                (height * downscaleFactor).toInt(),
                Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        val matrix = Matrix()
        matrix.preScale(downscaleFactor, downscaleFactor)
        canvas.setMatrix(matrix)
        view.draw(canvas)
        return bitmap
    }
}