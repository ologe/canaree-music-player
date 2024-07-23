package dev.olog.presentation.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import dev.olog.media.model.PlayerShuffleMode
import dev.olog.presentation.R
import dev.olog.shared.android.extensions.colorAccent
import dev.olog.shared.android.extensions.getAnimatedVectorDrawable
import dev.olog.shared.android.extensions.isDarkMode
import dev.olog.shared.android.extensions.onEnd
import dev.olog.shared.lazyFast
import dev.olog.shared.android.theme.hasPlayerAppearance
import dev.olog.shared.widgets.ColorDelegateImpl
import dev.olog.shared.widgets.IColorDelegate

class ShuffleButton(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageButton(context, attrs), IColorDelegate by ColorDelegateImpl {

    private var enabledColor: Int
    private var shuffleMode = PlayerShuffleMode.NOT_SET

    private val playerAppearance by lazyFast { context.hasPlayerAppearance() }
    private val isDarkMode by lazyFast { context.isDarkMode() }
    private val visibilityEnhancement = ButtonVisibilityEnhancement(context)

    init {
        setImageResource(R.drawable.vd_shuffle)
        enabledColor = context.colorAccent()
        background = null
        if (!isInEditMode){
            val defaultColor = getDefaultColor(context, playerAppearance, isDarkMode)
            setColorFilter(defaultColor)
            visibilityEnhancement.setColor(defaultColor)
        }
    }

    fun cycle(state: PlayerShuffleMode) {
        if (this.shuffleMode != state) {
            animate(state)
            this.shuffleMode = state
        }
    }

    fun updateSelectedColor(@ColorInt color: Int?) {
        enabledColor = color ?: context.colorAccent()

        if (shuffleMode == PlayerShuffleMode.ENABLED) {
            visibilityEnhancement.setColor(enabledColor)
            setColorFilter(enabledColor)
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        visibilityEnhancement.draw(canvas, width)
    }

    private fun animate(state: PlayerShuffleMode) {
        val endColor = when (state) {
            PlayerShuffleMode.NOT_SET -> return
            PlayerShuffleMode.DISABLED -> getDefaultColor(context, playerAppearance, isDarkMode)
            PlayerShuffleMode.ENABLED -> enabledColor
        }

        if (shuffleMode == PlayerShuffleMode.NOT_SET) {
            updateState(
                state = state,
                drawable = ContextCompat.getDrawable(context, R.drawable.vd_shuffle),
                color = endColor
            )
            return
        }

        val hideDrawable = context.getAnimatedVectorDrawable(R.drawable.shuffle_hide)
        setImageDrawable(hideDrawable)
        hideDrawable.onEnd {
            val showDrawable = context.getAnimatedVectorDrawable(R.drawable.shuffle_show)
            updateState(state, showDrawable, endColor)
            showDrawable.start()
        }
        hideDrawable.start()
    }

    private fun updateState(
        state: PlayerShuffleMode,
        drawable: Drawable?,
        @ColorInt color: Int,
    ) {
        setColorFilter(color)
        setImageDrawable(drawable)
        visibilityEnhancement.show(state == PlayerShuffleMode.ENABLED)
        visibilityEnhancement.setColor(color)
    }

}