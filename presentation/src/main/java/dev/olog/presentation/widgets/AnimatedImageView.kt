package dev.olog.presentation.widgets

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.support.v7.widget.AppCompatImageButton
import android.util.AttributeSet
import android.view.ViewPropertyAnimator
import dev.olog.presentation.R
import dev.olog.presentation.utils.isMarshmallow

class AnimatedImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0

) : AppCompatImageButton(context, attrs, defStyleAttr) {

    private val avd: AnimatedVectorDrawable
    private val animator: ViewPropertyAnimator = animate()

    init {

        val a = context.theme.obtainStyledAttributes(
                attrs, R.styleable.AnimatedImageView, 0, 0)

        avd = a.getDrawable(R.styleable.AnimatedImageView_avd) as AnimatedVectorDrawable
        setImageDrawable(avd)
        a.recycle()
    }

    fun playAnimation() {
        stopPreviousAnimation()
        avd.start()
    }

    private fun stopPreviousAnimation() {
        if (isMarshmallow()) {
            avd.reset()
        } else
            avd.stop()
    }

    fun toggleVisibility(show: Boolean) {
        isEnabled = show

        animator.cancel()
        animator.alpha(if (show) 1f else 0f)
    }

}
