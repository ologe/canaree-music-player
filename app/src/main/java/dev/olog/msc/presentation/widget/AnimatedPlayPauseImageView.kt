package dev.olog.msc.presentation.widget

import android.content.Context
import android.graphics.Color
import android.support.annotation.Keep
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatImageButton
import android.util.AttributeSet
import dev.olog.msc.R
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.utils.k.extension.getAnimatedVectorDrawable
import dev.olog.msc.utils.k.extension.isPortrait
import dev.olog.msc.utils.k.extension.textColorTertiary

@Keep
class AnimatedPlayPauseImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : AppCompatImageButton(context, attrs, 0) {

    private val playAnimation = context.getAnimatedVectorDrawable(R.drawable.avd_playpause_play_to_pause)
    private val pauseAnimation = context.getAnimatedVectorDrawable(R.drawable.avd_playpause_pause_to_play)

    init {
        if (AppTheme.isDarkTheme()){
            setColorFilter(0xFF_FFFFFF.toInt())
        }

        setupBackground(false)
    }

    private fun setupBackground(play: Boolean) {
        val drawableInt = if (play) R.drawable.vd_playpause_pause else R.drawable.vd_playpause_play
        val drawable = ContextCompat.getDrawable(context, drawableInt)
        setImageDrawable(drawable)
    }

    fun setDefaultColor(){
        setColorFilter(getDefaultColor())
    }

    fun useLightImage(){
        setColorFilter(0xFF_F5F5F5.toInt())
    }

    fun animationPlay(animate: Boolean) {
        if (animate){
            setAvd(playAnimation)
        } else {
            setImageResource(R.drawable.vd_playpause_pause)
        }
    }

    fun animationPause(animate: Boolean) {
        if (animate){
            setAvd(pauseAnimation)
        } else {
            setImageResource(R.drawable.vd_playpause_play)
        }
    }

    private fun setAvd(avd: AnimatedVectorDrawableCompat){
        setImageDrawable(avd)
        avd.start()
    }

    private fun getDefaultColor(): Int{
        return when {
            context.isPortrait && AppTheme.isClean() && !AppTheme.isDarkTheme() -> 0xFF_8d91a6.toInt()
            AppTheme.isFullscreen() || AppTheme.isDarkTheme() -> Color.WHITE
            else -> context.textColorTertiary()
        }
    }

}
