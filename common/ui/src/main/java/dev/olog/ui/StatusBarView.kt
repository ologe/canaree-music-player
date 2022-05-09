package dev.olog.ui

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import dev.olog.shared.extension.setHeight
import dev.olog.shared.isMarshmallow

/**
 * Custom status bar to handle device notch
 */
class StatusBarView(
    context: Context,
    attrs: AttributeSet
) : View(context, attrs) {

    companion object {
        // workaround: caching value because when changing page in bottom navigation view
        // setOnApplyWindowInsetsListener is not called
        @JvmStatic
        var viewHeight = -1
    }

    init {
        if (!isInEditMode){
            if (isMarshmallow()){
                setBackgroundColor(context.colorSurface())
            } else {
                setBackgroundColor(Color.BLACK)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isInEditMode){
            return
        }
        if (viewHeight == -1){
            setOnApplyWindowInsetsListener { _, insets ->
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