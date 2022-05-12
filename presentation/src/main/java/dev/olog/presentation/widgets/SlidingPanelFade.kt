package dev.olog.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.math.MathUtils.clamp
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.platform.HasSlidingPanel
import dev.olog.presentation.R
import dev.olog.ui.extension.collapse
import dev.olog.shared.extension.dip
import dev.olog.shared.extension.findInContext
import dev.olog.shared.extension.isTablet
import dev.olog.shared.extension.lazyFast
import dev.olog.ui.scrimBackground

class SlidingPanelFade(
        context: Context,
        attrs: AttributeSet
) : View(context, attrs) {

    private val fragmentContainer by lazyFast {
        (context.findInContext<FragmentActivity>()).findViewById<View>(R.id.fragmentContainer)
    }

    private val slidingPanel by lazyFast { context.findInContext<HasSlidingPanel>().getSlidingPanel() }
    private val isTablet = context.isTablet

    var parallax = context.dip(20)

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