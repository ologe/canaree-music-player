package dev.olog.presentation.utils.animation

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.support.transition.Fade
import android.support.transition.TransitionValues
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup

class CircularReveal(
        private val icon: View,
        private val fromColor: Int = 0xfff0f0f0.toInt(), // grey
        private val toColor: Int = Color.WHITE

) : Fade() {

    override fun onAppear(sceneRoot: ViewGroup, view: View,
                          startValues: TransitionValues?, endValues: TransitionValues?): Animator {

        val set = AnimatorSet()
        set.playTogether(
                super.onAppear(sceneRoot, view, startValues, endValues),
                createCircularReveal(view, true),
                animateBackgroundColor(view, fromColor, toColor)
        )
        set.duration = 350
        return set
    }

    override fun onDisappear(sceneRoot: ViewGroup, view: View,
                             startValues: TransitionValues?, endValues: TransitionValues?): Animator {

//        val set = AnimatorSet()
//        set.playTogether(
//                createCircularReveal(view, false),
//                animateBackgroundColor(view, toColor, fromColor) // invert colors
//        )
//        set.duration = 250
//        set.addListener(object : Animator.AnimatorListener{
//            override fun onAnimationRepeat(animation: Animator?) {}
//
//            override fun onAnimationEnd(animation: Animator?) {
//                view.visibility = View.INVISIBLE
//            }
//
//            override fun onAnimationCancel(animation: Animator?) {}
//
//            override fun onAnimationStart(animation: Animator?) {}
//        })
//
//        return set
        return super.onDisappear(sceneRoot, view, startValues, endValues)
    }

    private fun createCircularReveal(view: View, isAppearing: Boolean) : Animator{
        val cx = (icon.x + icon.width / 2).toInt()
        val cy = (icon.y + icon.height / 2).toInt()
        val width = view.width
        val height = view.height

        val radius = Math.sqrt((width * width + height * height).toDouble()).toFloat()

        val initialRadius = if (isAppearing) 0f else radius
        val finalRadius = if (isAppearing) radius else 0f

        return ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, finalRadius)
    }

    private fun animateBackgroundColor(view: View, fromColor: Int, toColor: Int): Animator {
        val valueAnimator = ValueAnimator()
        valueAnimator.setIntValues(fromColor, toColor)
        valueAnimator.setEvaluator(ArgbEvaluator())
        valueAnimator.addUpdateListener { view.setBackgroundColor(it.animatedValue as Int) }
        return valueAnimator
    }

}