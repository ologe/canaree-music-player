package dev.olog.presentation.widgets.parallax

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import com.google.android.material.math.MathUtils
import dev.olog.presentation.R
import dev.olog.shared.utils.clamp
import dev.olog.shared.widgets.ForegroundImageView

private const val DEFAULT_PARALLAX = 10
private const val MAX_ALPHA = 40 // .3f

class ParallaxImageView(
    context: Context,
    attrs: AttributeSet
) : ForegroundImageView(context, attrs) {

    private var scrimColor = Color.LTGRAY
    private val paint = Paint()

    private var parallax: Int = 0
    private var maxScrimAlpha = 0f

    private var totalListTranslationY = 0

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ParallaxImageView)
        parallax = a.getInt(
            R.styleable.ParallaxImageView_parallax,
            DEFAULT_PARALLAX
        )
        maxScrimAlpha = a.getInt(
            R.styleable.ParallaxImageView_max_scrim_alpha_255,
            MAX_ALPHA
        ).toFloat()
        a.recycle()

        // start transparent
        paint.color = scrimColor
        paint.alpha = 0
    }

    override fun onDraw(canvas: Canvas) {
        drawParallax(canvas)
        super.onDraw(canvas)
        drawScrim(canvas)
    }

    private fun drawParallax(canvas: Canvas) {
        if (isInEditMode) {
            return
        }
        if (drawable != null) {
            val currentTranslationY = clamp(totalListTranslationY, 0, height) / parallax
            canvas.translate(translationX, -currentTranslationY.toFloat())
        }
    }

    private fun drawScrim(canvas: Canvas) {
        if (isInEditMode) {
            return
        }
        val max = height / 2
        val clamped = clamp(totalListTranslationY, 0, max)
        val ratio = clamped.toFloat() / max.toFloat()
        val newAlpha = MathUtils.lerp(0f, maxScrimAlpha, ratio)
        paint.alpha = newAlpha.toInt()
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }

    fun onScrollChanged(translationY: Int) {
        totalListTranslationY += translationY
        invalidate()
    }

    fun setScrimColor(color: Int) {
        paint.color = color
        invalidate()
    }


}