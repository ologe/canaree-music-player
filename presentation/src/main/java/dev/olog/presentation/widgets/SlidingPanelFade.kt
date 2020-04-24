package dev.olog.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.math.MathUtils.clamp
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.presentation.R
import dev.olog.feature.presentation.base.activity.HasSlidingPanel
import dev.olog.feature.presentation.base.extensions.dip
import dev.olog.feature.presentation.base.extensions.collapse
import dev.olog.shared.android.extensions.scrimBackground
import dev.olog.shared.lazyFast

class SlidingPanelFade(
        context: Context,
        attrs: AttributeSet
) : View(context, attrs) {

    private val fragmentContainer by lazyFast {
        (context as FragmentActivity).findViewById<View>(R.id.fragmentContainer)
    }

    private val slidingPanel by lazyFast { (context as HasSlidingPanel).getSlidingPanel() }

    var parallax = context.dip(20)

    init {
        if (!isInEditMode) {
            setBackgroundColor(context.scrimBackground())
        }
        alpha = 0f
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode) {
            slidingPanel.addBottomSheetCallback(slidingPanelCallback)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        slidingPanel.removeBottomSheetCallback(slidingPanelCallback)
    }

    private val slidingPanelCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        private val visibleStates = listOf(
            BottomSheetBehavior.STATE_DRAGGING,
            BottomSheetBehavior.STATE_SETTLING
        )

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            alpha = clamp(slideOffset * 1.5f, 0f, 1f)

            fragmentContainer.translationY = -(slideOffset * parallax)
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            isVisible = newState in visibleStates

            val setClickable = newState != BottomSheetBehavior.STATE_COLLAPSED
            if (setClickable){
                setOnClickListener { slidingPanel.collapse() }
            } else {
                setOnClickListener(null)
            }

            isClickable = setClickable
            isFocusable = isClickable
        }
    }

}