package dev.olog.msc.presentation.widget.playpause

import android.content.Context
import android.util.AttributeSet
import dev.olog.msc.presentation.widget.BottomAppBarFab

class BottomAppBarAnimatedPlayPauseImageViewFab(
        context: Context, attrs: AttributeSet

): BottomAppBarFab(context, attrs), IPlayPauseBehavior {

    private val behavior = PlayPauseBehaviorImpl(this)

    override fun animationPlay(animate: Boolean) {
        behavior.animationPlay(animate)
    }

    override fun animationPause(animate: Boolean) {
        behavior.animationPause(animate)
    }

}