package dev.olog.feature.presentation.base.adapter.animation

import android.content.Context
import android.view.View
import dev.olog.feature.presentation.base.R

class ScaleMoreInOnTouch(
        private val view: View

) : AnimateOnTouch() {

    override fun animate(context: Context) {
        setAnimationAndPlay(view, R.animator.scale_more_in)
    }

    override fun restore(context: Context) {
        setAnimationAndPlay(view, R.animator.restore)
    }
}