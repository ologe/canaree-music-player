package dev.olog.presentation.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageButton
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import dev.olog.lib.media.model.PlayerRepeatMode
import dev.olog.presentation.R
import dev.olog.shared.android.extensions.colorAccent
import dev.olog.shared.android.extensions.getAnimatedVectorDrawable
import dev.olog.shared.exhaustive
import dev.olog.shared.widgets.ColorDelegateImpl
import dev.olog.shared.widgets.IColorDelegate

class RepeatButton(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageButton(context, attrs), IColorDelegate by ColorDelegateImpl {

    private var enabledColor: Int
    private var repeatMode: PlayerRepeatMode? = null

    init {
        setImageResource(R.drawable.vd_repeat)
        enabledColor = context.colorAccent()
        background = null
        if (!isInEditMode){
            val defaultColor = getDefaultColor(context)
            setColorFilter(defaultColor)
        }
    }

    fun cycle(state: PlayerRepeatMode) {
        if (this.repeatMode != state) {
            this.repeatMode = state
            when (state) {
                PlayerRepeatMode.NONE -> repeatNone()
                PlayerRepeatMode.ONE -> repeatOne()
                PlayerRepeatMode.ALL -> repeatAll()
            }.exhaustive
        }
    }

    fun updateSelectedColor(color: Int) {
        this.enabledColor = color

        if (repeatMode != PlayerRepeatMode.NONE) {
            setColorFilter(this.enabledColor)
        }
    }

    private fun repeatNone() {
        val defaultColor = getDefaultColor(context)
        animateAvd(defaultColor, R.drawable.repeat_hide_one, R.drawable.repeat_show)
    }

    private fun repeatOne() {
        animateAvd(enabledColor, R.drawable.repeat_hide, R.drawable.repeat_show_one)
    }

    private fun repeatAll() {
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

}