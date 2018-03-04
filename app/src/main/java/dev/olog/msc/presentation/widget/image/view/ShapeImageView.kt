package dev.olog.msc.presentation.widget.image.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.preference.PreferenceManager
import android.util.AttributeSet
import android.view.View
import androidx.graphics.drawable.toBitmap
import dev.olog.msc.R
import dev.olog.msc.presentation.widget.ForegroundImageView
import dev.olog.msc.utils.k.extension.dip

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
        val a = context.obtainStyledAttributes(R.styleable.RoundedCornerImageView)
        radius = a.getInt(R.styleable.RoundedCornerImageView_cornerRadius, DEFAULT_RADIUS)
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
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val value = prefs.getString(context.getString(R.string.prefs_icon_shape_key), context.getString(R.string.prefs_icon_shape_rounded))
            mask = when (value){
                context.getString(R.string.prefs_icon_shape_rounded) -> {
                    setLayerType(View.LAYER_TYPE_HARDWARE, null)
                    val drawable = ContextCompat.getDrawable(context, R.drawable.shape_rounded_corner)!! as GradientDrawable
                    drawable.cornerRadius = context.dip(radius).toFloat()
                    drawable.toBitmap(width, height)
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