package dev.olog.shared.widgets.playpause

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import dev.olog.shared.extensions.colorControlNormal
import dev.olog.shared.extensions.isDarkMode
import dev.olog.shared.extensions.lazyFast
import dev.olog.shared.theme.hasPlayerAppearance

class AnimatedPlayPauseImageView(
        context: Context,
        attrs: AttributeSet

) : AppCompatImageButton(context, attrs), IPlayPauseBehavior {

    private val playerAppearance by lazyFast { context.hasPlayerAppearance() }
    private val behavior = PlayPauseBehaviorImpl(this)

    private val isDarkMode by lazyFast { context.isDarkMode() }

    fun setDefaultColor() {
        setColorFilter(getDefaultColor())
    }

    fun useLightImage() {
        setColorFilter(0xFF_F5F5F5.toInt())
    }

    override fun animationPlay(animate: Boolean) {
        behavior.animationPlay(animate)
    }

    override fun animationPause(animate: Boolean) {
        behavior.animationPause(animate)
    }

    private fun getDefaultColor(): Int {
        return when {
            playerAppearance.isClean() && !isDarkMode -> 0xFF_8d91a6.toInt()
            playerAppearance.isFullscreen() || isDarkMode -> Color.WHITE
            else -> context.colorControlNormal()
        }
    }

}
