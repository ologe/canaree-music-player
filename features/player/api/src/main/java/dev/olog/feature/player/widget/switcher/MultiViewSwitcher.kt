package dev.olog.feature.player.widget.switcher

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ViewAnimator

open class MultiViewSwitcher(
    context: Context,
    attrs: AttributeSet
) : ViewAnimator(context, attrs) {

    fun getNextView(): View {
        var nextChild = displayedChild + 1
        if (nextChild >= childCount) {
            nextChild = 0
        }
        return getChildAt(nextChild)
    }

    fun getPreviousView(): View {
        var nextChild = displayedChild - 1
        if (nextChild <= 0) {
            nextChild = childCount - 1
        }
        return getChildAt(nextChild)
    }

}