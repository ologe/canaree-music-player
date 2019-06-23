package dev.olog.msc.presentation.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.ViewPropertyAnimator
import androidx.appcompat.widget.AppCompatImageButton
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import dev.olog.msc.R
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.shared.getAnimatedVectorDrawable
import dev.olog.shared.isDarkMode
import dev.olog.shared.lazyFast
import dev.olog.shared.textColorTertiary

class AnimatedImageView(
    context: Context,
    attrs: AttributeSet

) : AppCompatImageButton(context, attrs) {

    private val avd: AnimatedVectorDrawableCompat
    private val animator: ViewPropertyAnimator = animate()

    private val isDarkMode by lazyFast { context.isDarkMode() }

    init {
        val a = context.theme.obtainStyledAttributes(
            attrs, R.styleable.AnimatedImageView, 0, 0
        )

        val resId = a.getResourceId(R.styleable.AnimatedImageView_avd, -1)
        avd = context.getAnimatedVectorDrawable(resId)
        setImageDrawable(avd)
        a.recycle()
    }

    fun setDefaultColor() {
        setColorFilter(getDefaultColor())
    }

    fun useLightImage() {
        setColorFilter(0xFF_F5F5F5.toInt())
    }

    fun playAnimation() {
        stopPreviousAnimation()
        avd.start()
    }

    private fun stopPreviousAnimation() {
        avd.stop()
    }

    fun updateVisibility(show: Boolean) {
        isEnabled = show

        animator.cancel()
        animator.alpha(if (show) 1f else 0f)
    }

    private fun getDefaultColor(): Int {
        return when {
            AppTheme.isCleanTheme() && !isDarkMode -> 0xFF_8d91a6.toInt()
            AppTheme.isFullscreenTheme() || isDarkMode -> Color.WHITE
            else -> context.textColorTertiary()
        }
    }

}
