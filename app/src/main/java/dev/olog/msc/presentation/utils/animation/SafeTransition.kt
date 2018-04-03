package dev.olog.msc.presentation.utils.animation

import android.support.v4.app.Fragment
import android.transition.Transition
import javax.inject.Inject

class SafeTransition @Inject constructor(): Transition.TransitionListener {

    var isAnimating = false

    fun execute(fragment: Fragment, transition: Transition){
        fragment.enterTransition = transition
        transition.addListener(this)
    }

    override fun onTransitionStart(transition: Transition) {
        isAnimating = true
    }

    override fun onTransitionEnd(transition: Transition) {
        isAnimating = false
    }

    override fun onTransitionCancel(transition: Transition) {
        isAnimating = false
    }

    override fun onTransitionResume(transition: Transition) {
        isAnimating = true
    }

    override fun onTransitionPause(transition: Transition) {
        isAnimating = false
    }
}