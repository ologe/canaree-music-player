package dev.olog.msc.presentation.widget.image.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.support.v7.preference.PreferenceManager
import android.util.AttributeSet
import androidx.graphics.drawable.toBitmap
import dev.olog.msc.R
import dev.olog.msc.presentation.widget.ForegroundImageView
import java.lang.ref.WeakReference

private const val DEFAULT_RADIUS = 5
private val X_FERMO_MODE = PorterDuffXfermode(PorterDuff.Mode.DST_IN)

class RoundedCornerImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : ForegroundImageView(context, attrs){

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var maskBitmap : Bitmap? = null
    private var weakBitmap : WeakReference<Bitmap>? = null

    init {
//        val a = context.obtainStyledAttributes(R.styleable.RoundedCornerImageView)
//        val radius = a.getInt(R.styleable.RoundedCornerImageView_cornerRadius, DEFAULT_RADIUS)
//        val drawable = ContextCompat.getDrawable(context, R.drawable.rounded_corners_drawable) as GradientDrawable
//        drawable.cornerRadius = context.dip(radius).toFloat()
//        background = drawable
//        clipToOutline = true
//        a.recycle()
//        outlineProvider = RoundedOutlineProvider()

    }

    override fun invalidate() {
        weakBitmap?.clear()
        maskBitmap?.recycle()
        super.invalidate()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val i = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)

        var bitmap = weakBitmap?.get()
        if (bitmap == null || bitmap.isRecycled){
            val drawable = this.drawable
            drawable?.let {
                // allocation on onDraw is ok because is not happening that often
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val bitmapCanvas = Canvas(bitmap)
                drawable.setBounds(0, 0, width, height)
                drawable.draw(bitmapCanvas)

                if (maskBitmap == null || maskBitmap!!.isRecycled){
                    maskBitmap = getMask()
                }
                // draw bitmap
                paint.reset()
                paint.isFilterBitmap = false
                paint.xfermode = X_FERMO_MODE
                bitmapCanvas.drawBitmap(maskBitmap, 0f, 0f, paint)

                weakBitmap = WeakReference(bitmap!!)
            }
        }

        if (bitmap != null){
            paint.xfermode = null
            canvas.drawBitmap(bitmap, 0f, 0f, paint)
        }

        canvas.restoreToCount(i)

    }

    private fun getMask(): Bitmap? {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val maskDrawable = ContextCompat.getDrawable(context, R.drawable._squircle)!!
        return maskDrawable.toBitmap()
    }

}