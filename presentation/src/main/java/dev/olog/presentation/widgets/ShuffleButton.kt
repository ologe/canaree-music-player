package dev.olog.presentation.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageButton
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import dev.olog.media.model.PlayerShuffleMode
import dev.olog.platform.theme.hasPlayerAppearance
import dev.olog.presentation.R
import dev.olog.shared.extension.getAnimatedVectorDrawable
import dev.olog.shared.extension.isDarkMode
import dev.olog.shared.extension.lazyFast
import dev.olog.ui.ColorDelegateImpl
import dev.olog.ui.IColorDelegate
import dev.olog.ui.colorAccent

class ShuffleButton(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageButton(context, attrs), IColorDelegate by ColorDelegateImpl {

    private var enabledColor: Int
    private var shuffleMode = PlayerShuffleMode.NOT_SET

    private val playerAppearance by lazyFast { context.hasPlayerAppearance() }
    private val isDarkMode by lazyFast { context.isDarkMode() }

    init {
        setImageResource(R.drawable.vd_shuffle)
        enabledColor = context.colorAccent()
        background = null
        if (!isInEditMode){
            val defaultColor = getDefaultColor(context, playerAppearance, isDarkMode)
            setColorFilter(defaultColor)
        }
    }

    fun cycle(state: PlayerShuffleMode) {
        if (this.shuffleMode != state) {
            this.shuffleMode = state
            when (state) {
                PlayerShuffleMode.NOT_SET -> throw IllegalStateException("value not valid $state")
                PlayerShuffleMode.DISABLED -> disable()
                PlayerShuffleMode.ENABLED -> enable()
            }
        }
    }

    fun updateSelectedColor(color: Int) {
        this.enabledColor = color

        if (shuffleMode == PlayerShuffleMode.ENABLED) {
            setColorFilter(this.enabledColor)
        }
    }

    private fun enable() {
        animateAvd(enabledColor)
    }

    private fun disable() {
        val defaultColor = getDefaultColor(context, playerAppearance, isDarkMode)
        animateAvd(defaultColor)
    }

    private fun animateAvd(@ColorInt endColor: Int) {
        val hideDrawable = context.getAnimatedVectorDrawable(R.drawable.shuffle_hide)
        setImageDrawable(hideDrawable)
        hideDrawable.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
            override fun onAnimationEnd(drawable: Drawable?) {
                val showDrawable = context.getAnimatedVectorDrawable(R.drawable.shuffle_show)
                setColorFilter(endColor)
                setImageDrawable(showDrawable)
                showDrawable.start()
            }
        })
        hideDrawable.start()
    }

}