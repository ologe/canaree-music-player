package dev.olog.shared.animation

import android.animation.Animator
import android.animation.ObjectAnimator

open class SimpleAnimatorListener : Animator.AnimatorListener {
    override fun onAnimationRepeat(animator: Animator) {
    }

    override fun onAnimationEnd(animator: Animator) {
    }

    override fun onAnimationCancel(animator: Animator) {
    }

    override fun onAnimationStart(animator: Animator) {
    }
}

inline fun ObjectAnimator.doOnStart(crossinline action: (Animator) -> Unit){
    addListener(object : SimpleAnimatorListener(){
        override fun onAnimationStart(animator: Animator) {
            action(animator)
        }
    })
}

inline fun ObjectAnimator.doOnEnd(crossinline action: (Animator) -> Unit){
    addListener(object : SimpleAnimatorListener(){
        override fun onAnimationEnd(animator: Animator) {
            action(animator)
        }
    })
}