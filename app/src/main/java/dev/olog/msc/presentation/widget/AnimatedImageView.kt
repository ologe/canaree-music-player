package dev.olog.msc.presentation.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.AnimatedVectorDrawable
import android.support.annotation.Keep
import android.support.v7.widget.AppCompatImageButton
import android.util.AttributeSet
import android.view.ViewPropertyAnimator
import dev.olog.msc.R
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.utils.isMarshmallow
import dev.olog.msc.utils.k.extension.isPortrait
import dev.olog.msc.utils.k.extension.textColorTertiary

@Keep
class AnimatedImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : AppCompatImageButton(context, attrs, 0) {

    private val avd: AnimatedVectorDrawable
    private val animator: ViewPropertyAnimator = animate()

    init {
        if (AppTheme.isDarkTheme()){
            setColorFilter(0xFF_FFFFFF.toInt())
        }

        val a = context.theme.obtainStyledAttributes(
                attrs, R.styleable.AnimatedImageView, 0, 0)

        avd = a.getDrawable(R.styleable.AnimatedImageView_avd) as AnimatedVectorDrawable
        setImageDrawable(avd)
        a.recycle()
    }

    fun setDefaultColor(){
        setColorFilter(getDefaultColor())
    }

    fun useLightImage(){
        setColorFilter(0xFF_F5F5F5.toInt())
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

    fun updateVisibility(show: Boolean) {
        isEnabled = show

        animator.cancel()
        animator.alpha(if (show) 1f else 0f)
    }

    private fun getDefaultColor(): Int{
        return when {
            context.isPortrait && AppTheme.isClean() && !AppTheme.isDarkTheme() -> 0xFF_929cb0.toInt()
            AppTheme.isFullscreen() || AppTheme.isDarkTheme() -> Color.WHITE
            else -> textColorTertiary()
        }
    }

}
