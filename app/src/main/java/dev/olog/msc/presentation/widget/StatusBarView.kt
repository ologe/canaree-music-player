package dev.olog.msc.presentation.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.WindowInsets
import dev.olog.msc.R
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.utils.k.extension.dimen
import dev.olog.msc.utils.k.extension.dip
import dev.olog.msc.utils.k.extension.hasNotch
import dev.olog.msc.utils.k.extension.isPortrait
import dev.olog.shared.colorSurface
import dev.olog.shared.setHeight

/**
 * Custom status bar to handle device notch
 */
class StatusBarView : View {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private var viewHeight = 0

    override fun onApplyWindowInsets(insets: WindowInsets?): WindowInsets {
        viewHeight = insets?.systemWindowInsetTop ?: 0
        setHeight(viewHeight)
        return super.onApplyWindowInsets(insets)
    }

    private fun init() {
        setBackgroundColor(context.colorSurface())
    }

}