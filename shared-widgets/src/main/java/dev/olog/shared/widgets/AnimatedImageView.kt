package dev.olog.shared.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.ViewPropertyAnimator
import androidx.appcompat.widget.AppCompatImageButton
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import dev.olog.shared.android.extensions.getAnimatedVectorDrawable
import dev.olog.shared.android.extensions.isDarkMode
import dev.olog.shared.android.theme.themeManager
import dev.olog.shared.lazyFast

class AnimatedImageView(
    context: Context,
    attrs: AttributeSet

) : AppCompatImageButton(context, attrs), IColorDelegate by ColorDelegateImpl {

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
        val playerAppearance = context.themeManager.playerAppearance
        val defaultColor = getDefaultColor(context, playerAppearance, isDarkMode)
        setColorFilter(defaultColor)
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

}
