package dev.olog.feature.presentation.base.transition

import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionSet

object FastAutoTransition : TransitionSet() {

    private const val DURATION = 250L

    init {
        ordering = ORDERING_SEQUENTIAL
        duration = DURATION
        addTransition(Fade(Fade.OUT))
        addTransition(ChangeBounds())
        addTransition(Fade(Fade.IN))
    }

}