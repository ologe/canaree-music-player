package dev.olog.presentation.activity_main

import android.support.v7.widget.RecyclerView
import android.view.View
import com.sothree.slidinguppanel.ScrollableViewHelper

class NestedScrollHelper : ScrollableViewHelper() {

    override fun getScrollableViewScrollPosition(scrollableView: View, isSlidingUp: Boolean): Int {

        if (scrollableView is RecyclerView && scrollableView.childCount > 0) {
            if (scrollableView.adapter == null) return 0
            if (scrollableView.layoutManager == null) return 0
            val lm = scrollableView.layoutManager
            if (isSlidingUp) {
                if (scrollableView.canScrollVertically(-1)) {
                    return 1
                }
            } else {
                val lastChild = scrollableView.getChildAt(scrollableView.childCount - 1)
                // Approximate the scroll position based on the bottom child and the last visible item
                return (scrollableView.adapter.itemCount - 1) * lm.getDecoratedMeasuredHeight(lastChild) + lm.getDecoratedBottom(lastChild) - scrollableView.bottom
            }
        }
        return 0
    }
}