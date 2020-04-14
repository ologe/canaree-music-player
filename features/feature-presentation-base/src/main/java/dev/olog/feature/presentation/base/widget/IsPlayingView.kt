package dev.olog.feature.presentation.base.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

private val interpolator = FastOutSlowInInterpolator()
private const val MIN_SCALE_Y = 0f
private const val MIN_SCALE_X = 0.75f

class IsPlayingView(
    context: Context,
    attrs: AttributeSet
) : View(context, attrs) {

    fun toggleVisibility(show: Boolean) {
        val scaleX = if (show) 1f else MIN_SCALE_X
        val scaleY = if (show) 1f else MIN_SCALE_Y
        val alpha = if (show) 1f else 0f
        this.scaleX = scaleX
        this.scaleY = scaleY
        this.alpha = alpha
    }

    fun animateVisibility(show: Boolean) {
        animate().cancel()
        if (show) {
            expand()
        } else {
            shrink()
        }
    }

    private fun shrink() {
        animate().scaleY(MIN_SCALE_Y)
            .scaleX(MIN_SCALE_X)
            .alpha(0f)
            .setInterpolator(interpolator)
    }

    private fun expand() {
        animate().scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setInterpolator(interpolator)
    }

}