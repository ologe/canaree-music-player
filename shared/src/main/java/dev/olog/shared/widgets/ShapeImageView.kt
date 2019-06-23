package dev.olog.shared.widgets

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import dev.olog.shared.R

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
        radius = a.getInt(R.styleable.RoundedCornersImageView_imageViewCornerRadius, DEFAULT_RADIUS)
        a.recycle()

        clipToOutline = true

        paint.xfermode = X_FERMO_MODE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        getMask()?.let { canvas.drawBitmap(it, 0f, 0f, paint) }
    }

    private fun getMask(): Bitmap? {
//        if (mask == null){
//            mask = when (AppConstants.IMAGE_SHAPE){
//                AppConstants.ImageShape.ROUND -> {
//                    setLayerType(View.LAYER_TYPE_HARDWARE, null)
//                    val drawable = ContextCompat.getDrawable(context, R.drawable.shape_rounded_corner)!! as GradientDrawable
//                    drawable.cornerRadius = context.dipf(radius)
//                    drawable.toBitmap(width, height, Bitmap.Config.ALPHA_8)
//                }
//                AppConstants.ImageShape.RECTANGLE -> {
//                    setLayerType(View.LAYER_TYPE_NONE, null)
//                    null
//                }
//            }
//        } TODO
        return mask
    }

}