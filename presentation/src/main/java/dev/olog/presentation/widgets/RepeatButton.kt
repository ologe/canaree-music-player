package dev.olog.presentation.widgets

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v4.media.session.PlaybackStateCompat
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageButton
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import dev.olog.presentation.R
import dev.olog.shared.extensions.*
import dev.olog.shared.theme.hasPlayerAppearance

class RepeatButton(
    context: Context,
    attrs: AttributeSet

) : AppCompatImageButton(context, attrs) {

    private var enabledColor: Int
    private var repeatMode = PlaybackStateCompat.REPEAT_MODE_NONE

    private val isDarkMode by lazyFast { context.isDarkMode() }

    init {
        setImageResource(R.drawable.vd_repeat)
        enabledColor = context.colorPrimary()
        background = null
        if (!isInEditMode){
            setColorFilter(getDefaultColor())
        }
    }

    fun cycle(state: Int) {
        if (this.repeatMode != state) {
            this.repeatMode = state
            when (state) {
                PlaybackStateCompat.REPEAT_MODE_NONE -> repeatNone()
                PlaybackStateCompat.REPEAT_MODE_ONE -> repeatOne()
                PlaybackStateCompat.REPEAT_MODE_ALL -> repeatAll()
            }
        }
    }

    fun updateSelectedColor(color: Int) {
        this.enabledColor = color

        if (repeatMode != PlaybackStateCompat.REPEAT_MODE_NONE) {
            setColorFilter(this.enabledColor)
        }
    }

    private fun repeatNone() {
        val color = getDefaultColor()
        animateAvd(color, R.drawable.repeat_hide_one, R.drawable.repeat_show)
    }

    private fun repeatOne() {
        alpha = 1f
        animateAvd(enabledColor, R.drawable.repeat_hide, R.drawable.repeat_show_one)
    }

    private fun repeatAll() {
        alpha = 1f
        animateAvd(enabledColor, R.drawable.repeat_hide, R.drawable.repeat_show)
    }

    private fun animateAvd(@ColorInt endColor: Int, @DrawableRes hideAnim: Int, @DrawableRes showAnim: Int) {
        val hideDrawable = context.getAnimatedVectorDrawable(hideAnim)
        setImageDrawable(hideDrawable)
        hideDrawable.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
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
        val playerAppearance = context.hasPlayerAppearance()
        return when {
            playerAppearance.isClean() && !isDarkMode -> 0xFF_8d91a6.toInt()
            playerAppearance.isFullscreen() -> Color.WHITE
            isDarkMode -> {
                alpha = .7f
                context.textColorSecondary()
            }
            else -> context.colorControlNormal()
        }
    }

}