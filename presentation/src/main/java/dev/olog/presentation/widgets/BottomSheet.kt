package dev.olog.presentation.widgets

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.shape.*
import dev.olog.feature.presentation.base.activity.HasSlidingPanel
import dev.olog.feature.presentation.base.extensions.dipf
import dev.olog.shared.android.extensions.colorBottomViews
import dev.olog.shared.android.theme.BottomSheetType
import dev.olog.shared.android.theme.themeManager
import dev.olog.shared.exhaustive
import dev.olog.shared.lazyFast

class BottomSheet(
    context: Context,
    attrs: AttributeSet
) : FrameLayout(context, attrs) {

    private val appearanceModel = ShapeAppearanceModel.Builder()
        .setAllCorners(RoundedCornerTreatment())
        .setTopRightCorner(CornerFamily.ROUNDED, context.dipf(12))
        .setTopLeftCorner(CornerFamily.ROUNDED, context.dipf(12))
        .build()
    private val backgroundDrawable = MaterialShapeDrawable(appearanceModel)

    private val bottomSheetType by lazyFast {
        context.themeManager.bottomSheetType
    }

    init {
        if (!isInEditMode) {
            when (bottomSheetType) {
                BottomSheetType.DEFAULT -> setupDefault()
                BottomSheetType.FLOATING -> setupElevated()
            }
        }
    }

    private fun setupDefault() {
        elevation = context.dipf(8)
        background = backgroundDrawable
        backgroundTintList = ColorStateList.valueOf(context.colorBottomViews())
    }

    private fun setupElevated() {
        elevation = 0f
        background = null
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode) {
            (context as HasSlidingPanel).getSlidingPanel().addBottomSheetCallback(listener)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        (context as HasSlidingPanel).getSlidingPanel().removeBottomSheetCallback(listener)
    }

    private val listener = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            when (bottomSheetType) {
                BottomSheetType.FLOATING -> {
                    val alpha = 1f - slideOffset
                    background?.alpha = (255f * alpha).toInt()
                }
                BottomSheetType.DEFAULT -> {
                    backgroundDrawable.interpolation = 1f - slideOffset
                }
            }.exhaustive

        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {

        }
    }

}