package dev.olog.msc.presentation.widget.playpause

import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import dev.olog.msc.R
import dev.olog.shared.extensions.getAnimatedVectorDrawable

interface IPlayPauseBehavior {

    fun animationPlay(animate: Boolean)
    fun animationPause(animate: Boolean)
}

class PlayPauseBehaviorImpl(private val button: ImageButton): IPlayPauseBehavior {

    private val context = button.context

    private val playAnimation = context.getAnimatedVectorDrawable(R.drawable.avd_playpause_play_to_pause)
    private val pauseAnimation = context.getAnimatedVectorDrawable(R.drawable.avd_playpause_pause_to_play)

    init {
        val drawableInt = R.drawable.vd_playpause_play
        val drawable = ContextCompat.getDrawable(context, drawableInt)
        button.setImageDrawable(drawable)
    }

    override fun animationPlay(animate: Boolean) {
        if (animate){
            setAvd(playAnimation)
        } else {
            button.setImageResource(R.drawable.vd_playpause_pause)
        }
    }

    override fun animationPause(animate: Boolean) {
        if (animate){
            setAvd(pauseAnimation)
        } else {
            button.setImageResource(R.drawable.vd_playpause_play)
        }
    }

    private fun setAvd(avd: AnimatedVectorDrawableCompat){
        button.setImageDrawable(avd)
        avd.start()
    }

}