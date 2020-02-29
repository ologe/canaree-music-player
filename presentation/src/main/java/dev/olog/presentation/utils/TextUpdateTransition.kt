package dev.olog.presentation.utils

import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionSet

object TextUpdateTransition : TransitionSet() {

    const val DURATION = 250L

    init {
        ordering = ORDERING_SEQUENTIAL
        duration = DURATION
        addTransition(Fade(Fade.OUT))
        addTransition(ChangeBounds())
        addTransition(Fade(Fade.IN))
    }

}