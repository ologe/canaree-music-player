package dev.olog.msc.presentation.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.graphics.drawable.Animatable2Compat
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.support.v4.content.ContextCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.widget.AppCompatImageButton
import android.util.AttributeSet
import dev.olog.msc.R
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.utils.k.extension.getAnimatedVectorDrawable
import dev.olog.msc.utils.k.extension.isPortrait
import dev.olog.msc.utils.k.extension.textColorSecondary
import dev.olog.msc.utils.k.extension.textColorTertiary

class RepeatButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : AppCompatImageButton(context, attrs) {

    private val defaultEnabledColor: Int
    private var enabledColor: Int
    private var repeatMode = PlaybackStateCompat.REPEAT_MODE_NONE

    init {
        setImageResource(R.drawable.vd_repeat)
        defaultEnabledColor = if (AppTheme.isDarkTheme()){
            ContextCompat.getColor(context, R.color.accent_secondary)
        } else {
            ContextCompat.getColor(context, R.color.accent)
        }
        enabledColor = defaultEnabledColor
        setColorFilter(getDefaultColor())
    }

    fun cycle(state: Int){
        if (this.repeatMode != state){
            this.repeatMode = state
            when (state){
                PlaybackStateCompat.REPEAT_MODE_NONE -> repeatNone()
                PlaybackStateCompat.REPEAT_MODE_ONE -> repeatOne()
                PlaybackStateCompat.REPEAT_MODE_ALL -> repeatAll()
            }
        }
    }

    fun updateSelectedColor(color: Int){
        this.enabledColor = color

        if (repeatMode != PlaybackStateCompat.REPEAT_MODE_NONE){
            setColorFilter(this.enabledColor)
        }
    }

    private fun repeatNone(){
        val color = getDefaultColor()
        animateAvd(color, R.drawable.repeat_hide_one, R.drawable.repeat_show)
    }

    private fun repeatOne(){
        alpha = 1f
        animateAvd(enabledColor, R.drawable.repeat_hide, R.drawable.repeat_show_one)
    }

    private fun repeatAll(){
        alpha = 1f
        animateAvd(enabledColor, R.drawable.repeat_hide, R.drawable.repeat_show)
    }

    private fun animateAvd(@ColorInt endColor: Int, @DrawableRes hideAnim: Int, @DrawableRes showAnim: Int){
        val hideDrawable = context.getAnimatedVectorDrawable(hideAnim)
        setImageDrawable(hideDrawable)
        AnimatedVectorDrawableCompat.registerAnimationCallback(hideDrawable, object : Animatable2Compat.AnimationCallback(){
            override fun onAnimationEnd(drawable: Drawable?) {
                val showDrawable = context.getAnimatedVectorDrawable(showAnim)
                setColorFilter(endColor)
                setImageDrawable(showDrawable)
                showDrawable.start()
            }
        })
        hideDrawable.start()
    }

    private fun getDefaultColor(): Int {
        return when {
            context.isPortrait && AppTheme.isClean() && !AppTheme.isDarkTheme() -> 0xFF_929cb0.toInt()
            AppTheme.isFullscreen() -> Color.WHITE
            AppTheme.isDarkTheme() -> {
                alpha = .7f
                textColorSecondary()
            }
            else -> textColorTertiary()
        }
    }

}