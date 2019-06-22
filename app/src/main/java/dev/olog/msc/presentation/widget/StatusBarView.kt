package dev.olog.msc.presentation.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.WindowInsets
import dev.olog.shared.colorSurface
import dev.olog.shared.setHeight

/**
 * Custom status bar to handle device notch
 */
class StatusBarView(
    context: Context,
    attrs: AttributeSet
) : View(context, attrs) {

    init {
        setBackgroundColor(context.colorSurface())
    }

    private var viewHeight = 0

    override fun onApplyWindowInsets(insets: WindowInsets?): WindowInsets {
        viewHeight = insets?.systemWindowInsetTop ?: 0
        setHeight(viewHeight)
        return super.onApplyWindowInsets(insets)
    }

}