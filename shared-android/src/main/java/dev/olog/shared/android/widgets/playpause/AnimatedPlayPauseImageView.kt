package dev.olog.shared.android.widgets.playpause

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import dev.olog.shared.android.extensions.isDarkMode
import dev.olog.shared.lazyFast
import dev.olog.shared.android.theme.hasPlayerAppearance
import dev.olog.shared.android.widgets.ColorDelegateImpl
import dev.olog.shared.android.widgets.IColorDelegate

class AnimatedPlayPauseImageView(
        context: Context,
        attrs: AttributeSet

) : AppCompatImageButton(context, attrs),
    IPlayPauseBehavior,
    IColorDelegate by ColorDelegateImpl {

    private val playerAppearance by dev.olog.shared.lazyFast { context.hasPlayerAppearance() }
    private val behavior = PlayPauseBehaviorImpl(this)

    private val isDarkMode by dev.olog.shared.lazyFast { context.isDarkMode() }

    fun setDefaultColor() {
        val defaultColor = getDefaultColor(context, playerAppearance, isDarkMode)
        setColorFilter(defaultColor)
    }

    fun useLightImage() {
        setColorFilter(lightColor())
    }

    override fun animationPlay(animate: Boolean) {
        behavior.animationPlay(animate)
    }

    override fun animationPause(animate: Boolean) {
        behavior.animationPause(animate)
    }

}
