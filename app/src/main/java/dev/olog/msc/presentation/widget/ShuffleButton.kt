package dev.olog.msc.presentation.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.graphics.drawable.Animatable2Compat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.widget.AppCompatImageButton
import android.util.AttributeSet
import dev.olog.msc.R
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.utils.k.extension.*

class ShuffleButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : AppCompatImageButton(context, attrs) {

    private var enabledColor : Int
    private var shuffleMode = PlaybackStateCompat.SHUFFLE_MODE_NONE

    init {
        setImageResource(R.drawable.vd_shuffle)
        enabledColor = context.colorAccent()
        setColorFilter(getDefaultColor())
    }

    fun cycle(state: Int){
        if (this.shuffleMode != state){
            this.shuffleMode = state
            when (state){
                PlaybackStateCompat.SHUFFLE_MODE_NONE -> disable()
                else -> enable()
            }
        }
    }

    fun updateSelectedColor(color: Int){
        this.enabledColor = color

        if (shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL){
            setColorFilter(this.enabledColor)
        }
    }

    private fun enable(){
        alpha = 1f
        animateAvd(enabledColor)
    }

    private fun disable(){
        val color = getDefaultColor()
        animateAvd(color)
    }

    private fun animateAvd(@ColorInt endColor: Int){
        val hideDrawable = context.getAnimatedVectorDrawable(R.drawable.shuffle_hide)
        setImageDrawable(hideDrawable)
        hideDrawable.registerAnimationCallback(object : Animatable2Compat.AnimationCallback(){
            override fun onAnimationEnd(drawable: Drawable?) {
                val showDrawable = context.getAnimatedVectorDrawable(R.drawable.shuffle_show)
                setColorFilter(endColor)
                setImageDrawable(showDrawable)
                showDrawable.start()
            }
        })
        hideDrawable.start()
    }

    private fun getDefaultColor(): Int {
        return when {
            context.isPortrait && AppTheme.isClean() && !AppTheme.isDarkTheme() -> 0xFF_8d91a6.toInt()
            AppTheme.isFullscreen() -> Color.WHITE
            AppTheme.isDarkTheme() -> {
                alpha = .7f
                context.textColorSecondary()
            }
            else -> context.textColorTertiary()
        }
    }

}