package dev.olog.msc.presentation.widget

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.support.annotation.Keep
import android.support.v7.widget.AppCompatImageButton
import android.util.AttributeSet
import dev.olog.msc.R
import dev.olog.msc.utils.k.extension.getAnimatedVectorDrawable

@Keep
class AnimatedPlayPauseImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : AppCompatImageButton(context, attrs, 0) {

    private val playAnimation = context.getAnimatedVectorDrawable(R.drawable.avd_playpause_play_to_pause)
    private val pauseAnimation = context.getAnimatedVectorDrawable(R.drawable.avd_playpause_pause_to_play)

    init {
        setupBackground(false)
    }

    private fun setupBackground(play: Boolean) {
        val drawable = if (play) playAnimation else pauseAnimation
        setImageDrawable(drawable)
        drawable.jumpToCurrentState()
    }

    fun animationPlay(animate: Boolean) {
        if (playAnimation != drawable){
            setupAndAnimate(animate, playAnimation)
        }
    }

    fun animationPause(animate: Boolean) {
        setupAndAnimate(animate, pauseAnimation)
    }

    private fun setupAndAnimate(animate: Boolean, avd: AnimatedVectorDrawable) {
        setImageDrawable(avd)
        if (animate)
            avd.start()
        else
            avd.jumpToCurrentState()
    }

}
