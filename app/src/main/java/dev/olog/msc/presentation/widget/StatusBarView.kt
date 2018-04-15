package dev.olog.msc.presentation.widget

import android.app.Activity
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.utils.isMarshmallow
import dev.olog.msc.utils.k.extension.dimen
import dev.olog.msc.utils.k.extension.dip
import dev.olog.msc.utils.k.extension.hasNotch
import dev.olog.msc.utils.k.extension.px2dip

/**
 * Custom status bar to handle device notch
 */
class StatusBarView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null
) : View(context, attrs) {

    private val defaultStatusBarHeight = context.dimen(R.dimen.status_bar)
    private val statusBarHeightPlusNotch = context.dip(48)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = when {
            !isMarshmallow() -> 0
            hasNotch -> statusBarHeightPlusNotch
            else -> defaultStatusBarHeight
        }

//        super.onMeasure(widthMeasureSpec, height)
        setMeasuredDimension(widthMeasureSpec, height)
    }

    private val hasNotch: Boolean by lazy(LazyThreadSafetyMode.NONE) { this.hasNotch() }

}