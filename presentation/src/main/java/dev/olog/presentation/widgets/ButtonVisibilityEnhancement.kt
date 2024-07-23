package dev.olog.presentation.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.ColorInt
import dev.olog.shared.android.extensions.dipf

// dynamic colors can be non easily distinguishable from default colors
// in some cases, so draw additionally a dot above the main button
//
// this is meant for shuffle and repeat button only
class ButtonVisibilityEnhancement(context: Context) {

    private var show = false
    private val paint = Paint()
    private val cy = context.dipf(6)
    private val radius = context.dipf(2)

    fun setColor(@ColorInt color: Int) {
        paint.color = color
    }

    fun show(show: Boolean) {
        this.show = show
    }

    fun draw(canvas: Canvas, width: Int) {
        if (show) {
            canvas.drawCircle(width / 2f, cy, radius, paint)
        }
    }

}