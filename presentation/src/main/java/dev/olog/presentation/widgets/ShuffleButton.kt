package dev.olog.presentation.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageButton
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import dev.olog.feature.presentation.base.extensions.getAnimatedVectorDrawable
import dev.olog.lib.media.model.PlayerShuffleMode
import dev.olog.presentation.R
import dev.olog.shared.android.theme.themeManager
import dev.olog.feature.presentation.base.widget.ColorDelegateImpl
import dev.olog.feature.presentation.base.widget.IColorDelegate
import dev.olog.shared.android.dark.mode.isDarkMode
import dev.olog.shared.android.extensions.colorAccent
import java.lang.IllegalStateException

class ShuffleButton(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageButton(context, attrs), IColorDelegate by ColorDelegateImpl {

    private var enabledColor: Int
    private var shuffleMode = PlayerShuffleMode.NOT_SET

    private val isDarkMode = context.isDarkMode()

    init {
        setImageResource(R.drawable.vd_shuffle)
        enabledColor = context.colorAccent()
        background = null
        if (!isInEditMode){
            val playerAppearance = context.themeManager.playerAppearance
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
        val playerAppearance = context.themeManager.playerAppearance
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