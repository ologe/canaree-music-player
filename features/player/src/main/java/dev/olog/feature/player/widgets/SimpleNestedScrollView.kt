package dev.olog.feature.player.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.core.extensions.findActivity
import dev.olog.feature.presentation.base.activity.HasSlidingPanel
import dev.olog.shared.lazyFast

internal class SimpleNestedScrollView(
    context: Context,
    attrs: AttributeSet
) : FrameLayout(context, attrs) {

    private var isExpanded = false
    private val slidingPanel by lazyFast { (findActivity() as HasSlidingPanel).getSlidingPanel() }
    private val list: RecyclerView by lazy { getChildAt(0) as RecyclerView }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        slidingPanel?.addBottomSheetCallback(bottomSheetCallback)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        slidingPanel?.removeBottomSheetCallback(bottomSheetCallback)
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {

        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            isExpanded = slidingPanel?.state == BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return isExpanded && list.canScrollVertically(-1)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.action

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)
                list.dispatchTouchEvent(event)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                parent.requestDisallowInterceptTouchEvent(true)
                list.dispatchTouchEvent(event)
                return true
            }
            MotionEvent.ACTION_UP -> {
                parent.requestDisallowInterceptTouchEvent(false)
                list.dispatchTouchEvent(event)
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                parent.requestDisallowInterceptTouchEvent(false)
                list.dispatchTouchEvent(event)
                return true
            }
        }

        return super.onTouchEvent(event)
    }

}