package dev.olog.presentation.widgets.autoscroll

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewParent
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.presentation.interfaces.HasSlidingPanel
import dev.olog.shared.extensions.lazyFast

class SlidingParentAutoScrollTextView(
    context: Context,
    attrs: AttributeSet
) : AutoScrollTextView(context, attrs) {

    private val slidingPanel by lazyFast { (context as HasSlidingPanel).getSlidingPanel() }

    private val parentList: RecyclerView? by lazyFast { findParentRecyclerView() }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isSelected = slidingPanel.state == BottomSheetBehavior.STATE_EXPANDED
        slidingPanel.addPanelSlideListener(listener)
        parentList?.addOnScrollListener(recyclerViewListener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        slidingPanel.removePanelSlideListener(listener)
        parentList?.removeOnScrollListener(recyclerViewListener)
    }

    private fun findParentRecyclerView(): RecyclerView? {
        var currentParent: ViewParent? = parent
        var list: RecyclerView? = null
        while (currentParent != null) {
            if (currentParent is RecyclerView) {
                list = currentParent
            }
            currentParent = currentParent.parent
        }
        return list
    }

    private val recyclerViewListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            isSelected = newState == RecyclerView.SCROLL_STATE_IDLE &&
                    !recyclerView.canScrollVertically(-1)
        }
    }

    private val listener = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {

        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            isSelected = slidingPanel.state == BottomSheetBehavior.STATE_EXPANDED
        }
    }

}