package dev.olog.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.math.MathUtils.clamp
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.presentation.R
import dev.olog.presentation.interfaces.slidingPanel
import dev.olog.presentation.utils.collapse
import dev.olog.scrollhelper.MultiListenerBottomSheetBehavior
import dev.olog.platform.extension.dip
import dev.olog.platform.extension.findActivity
import dev.olog.platform.extension.isTablet
import dev.olog.platform.extension.scrimBackground
import dev.olog.shared.lazyFast

class SlidingPanelFade(
        context: Context,
        attrs: AttributeSet
) : View(context, attrs) {

    private val fragmentContainer by lazyFast {
        findActivity().findViewById<View>(R.id.fragmentContainer)
    }

    private val slidingPanel: MultiListenerBottomSheetBehavior<*>
        get() = context.slidingPanel
    private val isTablet = context.isTablet

    var parallax = context.dip(20)

    init {
        setBackgroundColor(context.scrimBackground())
        alpha = 0f
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        slidingPanel.addPanelSlideListener(slidingPanelCallback)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        slidingPanel.removePanelSlideListener(slidingPanelCallback)
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