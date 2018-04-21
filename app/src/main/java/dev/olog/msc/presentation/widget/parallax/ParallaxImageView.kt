package dev.olog.msc.presentation.widget.parallax

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.presentation.widget.image.view.ForegroundImageView
import dev.olog.msc.utils.k.extension.clamp

private const val DEFAULT_PARALLAX = .4f
private const val MAX_ALPHA = 85 //.3f

class ParallaxImageView(
        context: Context,
        attrs: AttributeSet? = null

) : ForegroundImageView(context, attrs) {

    private var scrimColor = Color.LTGRAY
    private val paint = Paint()

    private var parallax : Float

    init {
        val a = context.obtainStyledAttributes(R.styleable.ParallaxView)
        parallax = a.getFloat(R.styleable.ParallaxView_parallax, DEFAULT_PARALLAX)
        a.recycle()

        // start transparent
        paint.color = scrimColor
        paint.alpha = 0
    }

    fun translateY(root: View) {
        val diff = (height - root.bottom)

        translationY = diff.toFloat() * parallax

        val currentAlpha = clamp((diff * .1f).toInt(), 0, MAX_ALPHA)
        paint.alpha = currentAlpha
        invalidate()
    }

    fun setScrimColor(color: Int){
        paint.color = color
        paint.alpha = clamp(translationY.toInt(), 0, MAX_ALPHA)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }

}