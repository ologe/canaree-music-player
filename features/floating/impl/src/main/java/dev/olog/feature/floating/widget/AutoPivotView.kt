package dev.olog.feature.floating.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.doOnPreDraw

class AutoPivotView(
    context: Context,
    attrs: AttributeSet
) : View(context, attrs) {

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility == VISIBLE){
            doOnPreDraw {
                val yPivot = context.resources.displayMetrics.heightPixels.toFloat() - height.toFloat() / 2f
                val xPivot = context.resources.displayMetrics.widthPixels.toFloat() / 2f
                pivotY = yPivot
                pivotY = xPivot
            }
        }
    }

}