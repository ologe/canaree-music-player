package dev.olog.presentation.widgets

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageButton
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import dev.olog.media.model.PlayerShuffleMode
import dev.olog.presentation.R
import dev.olog.shared.extensions.*
import dev.olog.shared.theme.hasPlayerAppearance
import java.lang.IllegalStateException

class ShuffleButton(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageButton(context, attrs) {

    private var enabledColor: Int
    private var shuffleMode = PlayerShuffleMode.NOT_SET

    private val isDarkMode by lazyFast { context.isDarkMode() }

    init {
        setImageResource(R.drawable.vd_shuffle)
        enabledColor = context.colorAccent()
        background = null
        if (!isInEditMode){
            setColorFilter(getDefaultColor())
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
        alpha = 1f
        animateAvd(enabledColor)
    }

    private fun disable() {
        val color = getDefaultColor()
        animateAvd(color)
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