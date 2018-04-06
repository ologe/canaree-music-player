package dev.olog.msc.presentation.widget.image.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.utils.k.extension.dipf

private const val DEFAULT_RADIUS = 5

private val X_FERMO_MODE = PorterDuffXfermode(PorterDuff.Mode.DST_IN)

class ShapeImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : ForegroundImageView(context, attrs){

    private val radius : Int
    private var mask : Bitmap? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        val a = context.obtainStyledAttributes(R.styleable.RoundedCornersImageView)
        radius = a.getInt(R.styleable.RoundedCornersImageView_cornerRadius, DEFAULT_RADIUS)
        a.recycle()

        clipToOutline = true

        paint.xfermode = X_FERMO_MODE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        getMask()?.let { canvas.drawBitmap(it, 0f, 0f, paint) }
    }

    private fun getMask(): Bitmap? {
        if (mask == null){
            mask = when (AppConstants.ICON_SHAPE){
                context.getString(R.string.prefs_icon_shape_rounded) -> {
                    setLayerType(View.LAYER_TYPE_HARDWARE, null)
                    val drawable = ContextCompat.getDrawable(context, R.drawable.shape_rounded_corner)!! as GradientDrawable
                    drawable.cornerRadius = context.dipf(radius)
                    drawable.toBitmap(width, height, Bitmap.Config.ALPHA_8)
                }
                else -> {
                    setLayerType(View.LAYER_TYPE_NONE, null)
                    null
                }
            }
        }
        return mask
    }

}