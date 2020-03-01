package dev.olog.presentation.animations

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.presentation.interfaces.HasSlidingPanel
import dev.olog.shared.lazyFast

/**
 * Stub view that decides the overlay area where shared element will be drawn.
 * Needed because all fragments are drawn fullscreen due to `scroll helper` library, and there
 * are artifacts with `bottom sheet` and `bottom navigation`
 */
class SharedElementContainer(
    context: Context,
    attrs: AttributeSet
) : FrameLayout(context, attrs) {

    private val slidingPanel by lazyFast { (context as HasSlidingPanel).getSlidingPanel() }
    private val rect = Rect()

    private var state = BottomSheetBehavior.STATE_COLLAPSED

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        slidingPanel.addBottomSheetCallback(slidingPanelCallback)
        state = slidingPanel.state
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        slidingPanel.removeBottomSheetCallback(slidingPanelCallback)
        state = slidingPanel.state
    }

    override fun onDraw(canvas: Canvas) {
        // don't draw anything
    }

    private val slidingPanelCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            if (state == BottomSheetBehavior.STATE_COLLAPSED) {
                rect.set(0, 0, width, height - bottomSheet.top)
                clipBounds = rect
            }
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            state = newState
        }
    }

}