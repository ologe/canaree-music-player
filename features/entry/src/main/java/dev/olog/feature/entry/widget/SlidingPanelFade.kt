package dev.olog.feature.entry.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.math.MathUtils.clamp
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.feature.entry.R
import dev.olog.shared.android.collapse
import dev.olog.shared.android.extensions.dip
import dev.olog.shared.android.extensions.findActivity
import dev.olog.shared.android.extensions.isTablet
import dev.olog.shared.android.extensions.scrimBackground
import dev.olog.shared.android.slidingPanel
import dev.olog.shared.lazyFast

class SlidingPanelFade(
        context: Context,
        attrs: AttributeSet
) : View(context, attrs) {

    private val fragmentContainer by lazyFast {
        findActivity().findViewById<View>(R.id.fragmentContainer)
    }

    private val isTablet = context.isTablet

    var parallax = dip(20)

    init {
        setBackgroundColor(context.scrimBackground())
        alpha = 0f
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        slidingPanel.addBottomSheetCallback(slidingPanelCallback)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        slidingPanel.removeBottomSheetCallback(slidingPanelCallback)
    }

    private val slidingPanelCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            alpha = clamp(slideOffset * 1.5f, 0f, 1f)

            if (!isTablet){
                fragmentContainer.translationY = -(slideOffset * parallax)
            }
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
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