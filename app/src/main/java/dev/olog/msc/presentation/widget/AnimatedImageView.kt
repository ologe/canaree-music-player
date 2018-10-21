package dev.olog.msc.presentation.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.ViewPropertyAnimator
import androidx.annotation.Keep
import androidx.appcompat.widget.AppCompatImageButton
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import dev.olog.msc.R
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.utils.k.extension.getAnimatedVectorDrawable
import dev.olog.msc.utils.k.extension.isPortrait
import dev.olog.msc.utils.k.extension.textColorTertiary

@Keep
class AnimatedImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : AppCompatImageButton(context, attrs, 0) {

    private val avd: AnimatedVectorDrawableCompat
    private val animator: ViewPropertyAnimator = animate()

    init {
        if (AppTheme.isDarkTheme()){
            setColorFilter(0xFF_FFFFFF.toInt())
        }

        val a = context.theme.obtainStyledAttributes(
                attrs, R.styleable.AnimatedImageView, 0, 0)

        val resId = a.getResourceId(R.styleable.AnimatedImageView_avd, -1)
        avd = context.getAnimatedVectorDrawable(resId)
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
        avd.stop()
    }

    fun updateVisibility(show: Boolean) {
        isEnabled = show

        animator.cancel()
        animator.alpha(if (show) 1f else 0f)
    }

    private fun getDefaultColor(): Int{
        return when {
            context.isPortrait && AppTheme.isCleanTheme() && !AppTheme.isDarkTheme() -> 0xFF_8d91a6.toInt()
            AppTheme.isFullscreenTheme() || AppTheme.isDarkTheme() -> Color.WHITE
            else -> context.textColorTertiary()
        }
    }

}
