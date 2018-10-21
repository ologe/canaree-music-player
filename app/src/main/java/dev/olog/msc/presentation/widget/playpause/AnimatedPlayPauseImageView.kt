package dev.olog.msc.presentation.widget.playpause

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.annotation.Keep
import androidx.appcompat.widget.AppCompatImageButton
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.utils.k.extension.isPortrait
import dev.olog.msc.utils.k.extension.textColorTertiary

@Keep
class AnimatedPlayPauseImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : AppCompatImageButton(context, attrs, 0), IPlayPauseBehavior {

    private val behavior = PlayPauseBehaviorImpl(this)

    init {
        if (AppTheme.isDarkTheme()){
            setColorFilter(0xFF_FFFFFF.toInt())
        }
    }

    fun setDefaultColor(){
        setColorFilter(getDefaultColor())
    }

    fun useLightImage(){
        setColorFilter(0xFF_F5F5F5.toInt())
    }

    override fun animationPlay(animate: Boolean) {
        behavior.animationPlay(animate)
    }

    override fun animationPause(animate: Boolean) {
        behavior.animationPause(animate)
    }

    private fun getDefaultColor(): Int{
        return when {
            context.isPortrait && AppTheme.isCleanTheme() && !AppTheme.isDarkTheme() -> 0xFF_8d91a6.toInt()
            AppTheme.isFullscreenTheme() || AppTheme.isDarkTheme() -> Color.WHITE
            else -> context.textColorTertiary()
        }
    }

}
