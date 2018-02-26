package dev.olog.msc.presentation.utils.animation

import android.support.transition.Transition
import android.support.v4.app.Fragment
import javax.inject.Inject

class SafeTransition @Inject constructor(): Transition.TransitionListener {

    var isAnimating = true

    fun execute(fragment: Fragment, transition: Transition){
        fragment.enterTransition = transition
        transition.addListener(this)
    }

    override fun onTransitionStart(transition: Transition) {
    }

    override fun onTransitionEnd(transition: Transition) {
        isAnimating = false
    }

    override fun onTransitionCancel(transition: Transition) {
        isAnimating = false
    }

    override fun onTransitionResume(transition: Transition) {
    }

    override fun onTransitionPause(transition: Transition) {
    }
}