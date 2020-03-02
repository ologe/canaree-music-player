package dev.olog.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import dev.olog.shared.android.extensions.colorBackground
import dev.olog.shared.android.extensions.dipf
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
        setBackgroundColor(context.colorBackground())
    }

    private fun setupElevated() {
        elevation = 0f
        background = null
    }

}