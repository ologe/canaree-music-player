package dev.olog.presentation.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageButton
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import dev.olog.media.model.PlayerRepeatMode
import dev.olog.presentation.R
import dev.olog.shared.android.extensions.getAnimatedVectorDrawable
import dev.olog.shared.android.extensions.isDarkMode
import dev.olog.shared.android.extensions.onEnd
import dev.olog.shared.lazyFast
import dev.olog.shared.android.theme.hasPlayerAppearance
import dev.olog.shared.compose.theme.colorSchemeM3
import dev.olog.shared.compose.theme.legacyAccent
import dev.olog.shared.widgets.ColorDelegateImpl
import dev.olog.shared.widgets.IColorDelegate

class RepeatButton(
    context: Context,
    attrs: AttributeSet

) : AppCompatImageButton(context, attrs), IColorDelegate by ColorDelegateImpl {

    private var enabledColor: Int
    private var repeatMode = PlayerRepeatMode.NOT_SET

    private val playerAppearance by lazyFast { context.hasPlayerAppearance() }
    private val isDarkMode by lazyFast { context.isDarkMode() }
    private val visibilityEnhancement = ButtonVisibilityEnhancement(context)

    init {
        setImageResource(R.drawable.vd_repeat)
        enabledColor = context.colorSchemeM3.legacyAccent.toArgb()
        background = null
        if (!isInEditMode){
            val defaultColor = getDefaultColor(context, playerAppearance, isDarkMode)
            setColorFilter(defaultColor)
            visibilityEnhancement.setColor(defaultColor)
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        visibilityEnhancement.draw(canvas, width)
    }

    fun cycle(state: PlayerRepeatMode) {
        if (this.repeatMode != state) {
            animate(state)
            this.repeatMode = state
        }
    }

    fun updateSelectedColor(@ColorInt color: Int?) {
        this.enabledColor = color ?: context.colorSchemeM3.legacyAccent.toArgb()

        if (repeatMode != PlayerRepeatMode.NONE) {
            visibilityEnhancement.setColor(enabledColor)
            setColorFilter(enabledColor)
        }
    }

    private fun animate(state: PlayerRepeatMode) {
        val endColor = when (state) {
            PlayerRepeatMode.NOT_SET -> return
            PlayerRepeatMode.NONE -> getDefaultColor(context, playerAppearance, isDarkMode)
            PlayerRepeatMode.ONE,
            PlayerRepeatMode.ALL -> enabledColor
        }

        if (repeatMode == PlayerRepeatMode.NOT_SET) {
            val vd = when (state) {
                PlayerRepeatMode.NOT_SET -> return
                PlayerRepeatMode.NONE -> R.drawable.vd_repeat
                PlayerRepeatMode.ONE -> R.drawable.vd_repeat_one
                PlayerRepeatMode.ALL -> R.drawable.vd_repeat
            }
            updateState(
                state = state,
                drawable = ContextCompat.getDrawable(context, vd),
                color = endColor,
            )
            return
        }

        val hideAnim = when (state) {
            PlayerRepeatMode.NOT_SET -> return
            PlayerRepeatMode.NONE -> R.drawable.repeat_hide_one
            PlayerRepeatMode.ONE -> R.drawable.repeat_hide
            PlayerRepeatMode.ALL -> R.drawable.repeat_hide
        }

        val showAnim = when (state) {
            PlayerRepeatMode.NOT_SET -> return
            PlayerRepeatMode.NONE -> R.drawable.repeat_show
            PlayerRepeatMode.ONE -> R.drawable.repeat_show_one
            PlayerRepeatMode.ALL -> R.drawable.repeat_show
        }

        val hideDrawable = context.getAnimatedVectorDrawable(hideAnim)
        setImageDrawable(hideDrawable)
        hideDrawable.onEnd {
            val showDrawable = context.getAnimatedVectorDrawable(showAnim)
            updateState(state, showDrawable, endColor)
            showDrawable.start()
        }
        hideDrawable.start()
    }

    private fun updateState(
        state: PlayerRepeatMode,
        drawable: Drawable?,
        @ColorInt color: Int
    ) {
        setColorFilter(color)
        setImageDrawable(drawable)
        visibilityEnhancement.show(state == PlayerRepeatMode.ONE || state == PlayerRepeatMode.ALL)
        visibilityEnhancement.setColor(color)
    }

}