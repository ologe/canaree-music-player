package dev.olog.presentation.utils.touch

import android.content.Context
import android.view.View
import dev.olog.presentation.R

class ElevateSongOnTouch(
        private val view: View

) : ElevateOnTouch() {

    override fun elevate(context: Context) {
        setAnimationAndPlay(view, R.animator.raise_high_and_scale)
    }

    override fun restoreInitialPosition(context: Context) {
        setAnimationAndPlay(view, R.animator.restore)
    }
}