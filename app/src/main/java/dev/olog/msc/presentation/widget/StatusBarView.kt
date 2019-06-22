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

    companion object {
        // for some reason (bug maybe), setOnApplyWindowInsetsListener is called only once for all views
        // TODO can be a bug of current SDK
        var viewHeight = -1
    }

    init {
        setBackgroundColor(context.colorSurface())
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (viewHeight == -1){
            setOnApplyWindowInsetsListener { v, insets ->
                val height = insets?.systemWindowInsetTop ?: 0
                setHeight(height)
                viewHeight = height
                insets
            }
        } else {
            setHeight(viewHeight)
        }
    }

}