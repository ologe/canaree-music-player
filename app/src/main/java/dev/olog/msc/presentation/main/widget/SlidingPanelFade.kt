package dev.olog.msc.presentation.main.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.math.MathUtils.clamp
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.presentation.interfaces.HasSlidingPanel
import dev.olog.shared.lazyFast
import dev.olog.shared.dip
import dev.olog.msc.R

class SlidingPanelFade(
        context: Context,
        attrs: AttributeSet
) : View(context, attrs) {

    private val fragmentContainer by lazyFast { (context as FragmentActivity).findViewById<View>(R.id.fragmentContainer) }
    private val maxTranslation by lazyFast { context.dip(20) }

    init {
        setBackgroundColor(0x99_FFFFFF.toInt())
        alpha = 0f


    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (context as HasSlidingPanel).getSlidingPanel().addPanelSlideListener(slidingPanelCallback)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        (context as HasSlidingPanel).getSlidingPanel().removePanelSlideListener(slidingPanelCallback)
    }

    private val slidingPanelCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            alpha = clamp(slideOffset * 1.5f, 0f, 1f)
            fragmentContainer.translationY = -(slideOffset * maxTranslation)
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {

        }
    }

}