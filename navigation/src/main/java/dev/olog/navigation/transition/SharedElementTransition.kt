
package dev.olog.navigation.transition

import android.content.Context
import android.util.TypedValue
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialContainerTransform
import dev.olog.navigation.R

class SharedElementTransitionHold : Hold()

class SharedElementTransition(
    context: Context
) : MaterialContainerTransform(context) {

    init {
        containerColor = themeAttributeToColor(context, android.R.attr.colorBackground)
        drawingViewId = R.id.fragmentContainer
    }

    @Suppress("SameParameterValue")
    private fun themeAttributeToColor(
        context: Context,
        themeAttributeId: Int
    ): Int {
        val outValue = TypedValue()
        val theme = context.theme
        val resolved = theme.resolveAttribute(themeAttributeId, outValue, true)
        if (resolved) {
            val a = context.obtainStyledAttributes(outValue.resourceId, intArrayOf(themeAttributeId))
            val colorStateList = a.getColorStateList(0)
            a.recycle()
            if (colorStateList != null) {
                return colorStateList.defaultColor
            }
        }
        throw IllegalStateException("resource not found")
    }

}