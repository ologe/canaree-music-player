package dev.olog.msc.presentation.utils

import android.animation.AnimatorInflater
import android.content.Context
import android.support.annotation.AnimatorRes
import android.view.MotionEvent
import android.view.View

abstract class ElevateOnTouch : View.OnTouchListener {

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> animate(view.context)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                restore(view.context)
            }
        }
        return false
    }

    fun setAnimationAndPlay(viewToAnimate: View?, @AnimatorRes animatorId: Int) {
        if (viewToAnimate == null) return

        val context = viewToAnimate.context

        val animator = AnimatorInflater.loadAnimator(context, animatorId)
        animator.setTarget(viewToAnimate)
        animator.start()
    }

    protected abstract fun animate(context: Context)
    protected abstract fun restore(context: Context)

}
