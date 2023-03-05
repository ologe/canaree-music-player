package dev.olog.presentation.widgets.autoscroll

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.presentation.interfaces.slidingPanel
import dev.olog.scrollhelper.MultiListenerBottomSheetBehavior
import dev.olog.shared.android.extensions.findParentByType
import dev.olog.shared.lazyFast
import dev.olog.shared.widgets.AutoScrollTextView

class SlidingParentAutoScrollTextView(
    context: Context,
    attrs: AttributeSet
) : AutoScrollTextView(context, attrs) {

    private val slidingPanel: MultiListenerBottomSheetBehavior<*>
        get() = context.slidingPanel

    private val parentList: RecyclerView? by lazyFast { findParentByType<RecyclerView>() }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode){
            isSelected = slidingPanel.state == BottomSheetBehavior.STATE_EXPANDED
            slidingPanel.addPanelSlideListener(listener)
            parentList?.addOnScrollListener(recyclerViewListener)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (!isInEditMode){
            slidingPanel.removePanelSlideListener(listener)
            parentList?.removeOnScrollListener(recyclerViewListener)
        }
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