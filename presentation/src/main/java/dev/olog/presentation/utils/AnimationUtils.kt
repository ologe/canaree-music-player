package dev.olog.presentation.utils

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.support.v4.app.FragmentManager
import android.view.View
import android.view.ViewAnimationUtils

object AnimationUtils {

    fun startCircularReveal(background: View, icon: View,
                            fromColor: Int = 0xfff0f0f0.toInt(), toColor: Int = Color.WHITE){
        val cx = (icon.x + icon.width / 2).toInt()
        val cy = (icon.y + icon.height / 2).toInt()
        val width = background.width
        val height = background.height
        val finalRadius = Math.sqrt((width * width + height * height).toDouble()).toFloat()
        val anim = ViewAnimationUtils.createCircularReveal(background, cx, cy, 0f, finalRadius)
        anim.start()

        val valueAnimator = ValueAnimator()
        valueAnimator.setIntValues(fromColor, toColor)
        valueAnimator.setEvaluator(ArgbEvaluator())
        valueAnimator.addUpdateListener { background.setBackgroundColor(it.animatedValue as Int) }
        valueAnimator.start()
    }

    fun stopCircularReveal(background: View, icon: View,
                           fragmentManager: FragmentManager,
                           fromColor: Int = Color.WHITE, toColor: Int = 0xfff0f0f0.toInt()){
        val cx = (icon.x + icon.width / 2).toInt()
        val cy = (icon.y + icon.height / 2).toInt()
        val width = background.width
        val height = background.height
        val initialRadius = Math.sqrt((width * width + height * height).toDouble()).toFloat()
        val anim = ViewAnimationUtils.createCircularReveal(background, cx, cy, initialRadius, 0f)
        anim.start()

        val valueAnimator = ValueAnimator()
        valueAnimator.setIntValues(fromColor, toColor)
        valueAnimator.setEvaluator(ArgbEvaluator())
        valueAnimator.addUpdateListener { background.setBackgroundColor(it.animatedValue as Int) }
        valueAnimator.start()
        valueAnimator.addListener(object : Animator.AnimatorListener{

            override fun onAnimationEnd(animation: Animator) {
                onAnimationFinished(background, fragmentManager)
            }

            override fun onAnimationCancel(animation: Animator) {
                onAnimationFinished(background, fragmentManager)
            }

            override fun onAnimationRepeat(animation: Animator) {}
            override fun onAnimationStart(animation: Animator) {}
        })
    }

    private fun onAnimationFinished(background: View, fragmentManager: FragmentManager){
        background.alpha = 0f
        background.visibility = View.GONE
        fragmentManager.popBackStackImmediate()
    }

}