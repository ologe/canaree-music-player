package dev.olog.feature.presentation.base.widget.swipeable

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.graphics.ColorUtils

internal class SwipeableViewDebug(
    private val view: View,
    private val skipAreaDimension: Int
) {

    private val rectLeft = Rect()
    private val rectRight = Rect()

    private val paint = Paint().apply {
        color = ColorUtils.setAlphaComponent(Color.YELLOW, 100)
    }


    fun draw(canvas: Canvas){
        rectLeft.set(
            0,
            0,
            skipAreaDimension,
            view.height
        )
        canvas.drawRect(rectLeft, paint)

        rectRight.set(
            view.width - skipAreaDimension,
            0,
            view.width,
            view.height
        )
        canvas.drawRect(rectRight, paint)
    }

}