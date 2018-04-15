package dev.olog.msc.presentation.widget.image.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet

class BackgroundImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : AppCompatImageView(context, attrs) {

    private val paint = Paint()
    private val rect = Rect()

    init {
        paint.color = 0x99_00_00_00.toInt()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        rect.set(0,0, width, height)
        canvas.drawRect(rect, paint)
    }

}