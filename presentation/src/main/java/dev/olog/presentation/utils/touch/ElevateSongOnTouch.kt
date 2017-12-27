package dev.olog.presentation.utils.touch

import android.content.Context
import android.view.View
import dev.olog.presentation.R

class ElevateSongOnTouch(
        private val view: View,
        private val image: View

) : ElevateOnTouch() {

    override fun elevate(context: Context) {
        setAnimationAndPlay(view, R.animator.raise_low_and_scale)
        setAnimationAndPlay(image, R.animator.raise_high_and_scale)
    }

    override fun restoreInitialPosition(context: Context) {
        setAnimationAndPlay(view, R.animator.restore)
        setAnimationAndPlay(image, R.animator.restore)
    }
}