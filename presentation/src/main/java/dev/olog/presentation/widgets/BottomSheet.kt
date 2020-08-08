package dev.olog.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.core.extensions.findActivity
import dev.olog.feature.presentation.base.activity.HasSlidingPanel
import dev.olog.feature.presentation.base.extensions.dipf
import dev.olog.shared.android.extensions.colorBackground
import dev.olog.shared.android.theme.BottomSheetType
import dev.olog.shared.android.theme.themeManager

class BottomSheet(
    context: Context,
    attrs: AttributeSet
) : FrameLayout(context, attrs) {

    init {
        when (context.themeManager.bottomSheetType) {
            BottomSheetType.DEFAULT -> setupDefault()
            BottomSheetType.FLOATING -> setupElevated()
        }
    }

    private fun setupDefault() {
        elevation = context.dipf(8)
        // for some reason after inlining above `color` shadow is not drawn
        setBackgroundColor(context.colorBackground())
    }

    private fun setupElevated() {
        elevation = 0f
        background = null
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val themeManager = context.themeManager
        if (themeManager.playerAppearance.isMini && themeManager.bottomSheetType == BottomSheetType.DEFAULT) {
            (findActivity() as HasSlidingPanel).getSlidingPanel().addBottomSheetCallback(listener)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        val themeManager = context.themeManager
        if (themeManager.playerAppearance.isMini && themeManager.bottomSheetType == BottomSheetType.DEFAULT) {
            (findActivity() as HasSlidingPanel).getSlidingPanel().removeBottomSheetCallback(listener)
        }
    }

    private val listener = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            val alpha = 1f - slideOffset
            background?.alpha = (255f * alpha).toInt()
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {

        }
    }

}