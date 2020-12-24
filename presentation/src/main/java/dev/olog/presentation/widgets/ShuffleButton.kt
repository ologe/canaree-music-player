package dev.olog.presentation.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageButton
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import dev.olog.lib.media.model.PlayerShuffleMode
import dev.olog.presentation.R
import dev.olog.shared.android.extensions.colorAccent
import dev.olog.shared.android.extensions.getAnimatedVectorDrawable
import dev.olog.shared.exhaustive
import dev.olog.shared.widgets.ColorDelegateImpl
import dev.olog.shared.widgets.IColorDelegate

class ShuffleButton(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageButton(context, attrs), IColorDelegate by ColorDelegateImpl {

    private var enabledColor: Int
    private var shuffleMode: PlayerShuffleMode? = null

    init {
        setImageResource(R.drawable.vd_shuffle)
        enabledColor = context.colorAccent()
        background = null
        if (!isInEditMode){
            val defaultColor = getDefaultColor(context)
            setColorFilter(defaultColor)
        }
    }

    fun cycle(state: PlayerShuffleMode) {
        if (this.shuffleMode != state) {
            this.shuffleMode = state
            when (state) {
                PlayerShuffleMode.DISABLED -> disable()
                PlayerShuffleMode.ENABLED -> enable()
            }.exhaustive
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
        val defaultColor = getDefaultColor(context)
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