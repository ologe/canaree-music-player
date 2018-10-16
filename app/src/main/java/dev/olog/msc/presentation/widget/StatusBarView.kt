package dev.olog.msc.presentation.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.utils.k.extension.dimen
import dev.olog.msc.utils.k.extension.dip
import dev.olog.msc.utils.k.extension.hasNotch

/**
 * Custom status bar to handle device notch
 */
class StatusBarView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : View(context, attrs) {

    private val defaultStatusBarHeight = context.dimen(R.dimen.status_bar)
    private val statusBarHeightPlusNotch = context.dip(48)
    private var hasNotch = false

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        hasNotch = this.hasNotch()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = when {
            hasNotch -> statusBarHeightPlusNotch
            else -> defaultStatusBarHeight
        }

        setMeasuredDimension(widthMeasureSpec, height)
    }

}