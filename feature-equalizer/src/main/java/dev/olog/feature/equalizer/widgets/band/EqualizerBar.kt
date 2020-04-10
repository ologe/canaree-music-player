package dev.olog.feature.equalizer.widgets.band

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.math.MathUtils
import dev.olog.feature.presentation.base.extensions.dip
import kotlin.math.abs

private const val DEFAULT_ALPHA = 190 // .75f
private const val PRESSED_ALPHA = 255 // 1f
private const val DURATION = 150L

internal class EqualizerBar(
    context: Context,
    attrs: AttributeSet
) : View(context, attrs) {

    private var valueAnimator: ValueAnimator? = null

    var onProgressChanged: ((Float) -> Unit)? = null

    private var firstUpdate = true

    private var max = 100
    private var progress = max / 2
        set(value) {
            val newValue = MathUtils.clamp(value, 0, max)
            if (newValue != field) {
                field = newValue
                invalidate()
                onProgressChanged?.invoke(convertToExternalProgress(field))
            }
        }

    private var actualMin = Float.MIN_VALUE
    private var actualMax = Float.MAX_VALUE

    private var downY = -1f
    private val pressed: Boolean
        get() = downY > 0f

    private val rect = Rect()
    private val paint = Paint()

    private var defaultPadding = context.dip(2)
    private val padding: Int
        get() = if (pressed) 0 else defaultPadding

    init {
        isClickable = true
        isFocusable = true

        paint.color = backgroundTintList?.defaultColor ?: Color.BLACK
        paint.alpha = DEFAULT_ALPHA
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        valueAnimator?.cancel()
    }

    fun animateProgress(progress: Float, min: Float, max: Float) {
        actualMin = min
        actualMax = max
        if (firstUpdate) {
            firstUpdate = false
            this.progress = convertToInternalProgress(progress)
            return
        }

        valueAnimator?.cancel()
        valueAnimator = ValueAnimator.ofInt(this.progress, convertToInternalProgress(progress)).apply {
            duration = DURATION
            addUpdateListener {
                this@EqualizerBar.progress = it.animatedValue as Int
            }
            start()
        }
    }

    private fun convertToInternalProgress(progress: Float): Int {
        val adjustedMax = actualMax + abs(actualMin)
        val adjustProgress = progress + abs(actualMin)
        return (adjustProgress / adjustedMax * max).toInt()
    }

    private fun convertToExternalProgress(progress: Int): Float {
        val adjustedMax = actualMax + abs(actualMin)
        val adjustedProgress = progress * adjustedMax / max
        return adjustedProgress - abs(actualMin)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> return onDown(event.rawY)
            MotionEvent.ACTION_MOVE -> return onMove(event.rawY)
            MotionEvent.ACTION_UP -> return onUp()
        }

        return super.onTouchEvent(event)
    }

    private fun onDown(y: Float): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        downY = y
        invalidate()
        paint.alpha = PRESSED_ALPHA
        return true
    }

    private fun onMove(y: Float): Boolean {
        val diff = y - downY
        val deltaProgress = diff * max / height
        downY = y
        progress -= deltaProgress.toInt()
        invalidate()
        return true
    }

    private fun onUp(): Boolean {
        parent.requestDisallowInterceptTouchEvent(false)
        downY = -1f
        paint.alpha = DEFAULT_ALPHA
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        rect.set(
            padding,
            height - (height * progress.toFloat() / max.toFloat()).toInt() + padding,
            width - padding,
            height - padding
        )
        canvas.drawRect(rect, paint)
    }

}