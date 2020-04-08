package dev.olog.feature.presentation.base.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import dev.olog.feature.presentation.base.R

class DottedSeparator(
    context: Context,
    attrs: AttributeSet
) : View(context, attrs) {

    init {
        val isDarkMode = context.resources.getBoolean(R.bool.is_dark_mode)
        if (!isDarkMode) {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
            setBackgroundResource(R.drawable.dotted_line)
            backgroundTintList = ColorStateList.valueOf(Color.BLACK);
            alpha = .1f;
        }
    }
}