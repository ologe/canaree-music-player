package dev.olog.msc.presentation.widget

import android.content.Context
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.utils.isMarshmallow
import dev.olog.msc.utils.isP
import dev.olog.msc.utils.k.extension.dimen

/**
 * Custom status bar to handle device notch
 */
class StatusBarView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null
) : View(context, attrs) {

    private val defaultStatusBarHeight = context.dimen(R.dimen.status_bar)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = when {
            !isMarshmallow() -> 0
            isP() && hasNotch -> notchHeight
            else -> defaultStatusBarHeight
        }

//        super.onMeasure(widthMeasureSpec, height)
        setMeasuredDimension(widthMeasureSpec, height)
    }

    private val hasNotch: Boolean
        @RequiresApi(28)
        get() {
//            todo
//            val displayCutout = rootWindowInsets.displayCutout
//            return displayCutout != null
            return false
        }

    private val notchHeight: Int
        @RequiresApi(28)
        get() {
//            todo
//            val displayCutout = rootWindowInsets.displayCutout!!
//            return displayCutout.bounds.bounds.bottom
            return 0
        }

}