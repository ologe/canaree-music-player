package dev.olog.msc.presentation.utils.animation

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.transition.Fade
import android.transition.TransitionValues
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import androidx.core.animation.addListener
import dev.olog.msc.constants.AppConstants

private fun startColor(): Int{
    return if (AppConstants.IS_NIGHT_MODE){
        0xFF282828.toInt()
    } else {
        0xfff0f0f0.toInt()
    }
}

private fun endColor(): Int {
    return if (AppConstants.IS_NIGHT_MODE){
        0xFF131313.toInt()
    } else {
        Color.WHITE
    }
}

class CircularReveal(
        private val x: Int,
        private val y: Int,
        private val fromColor: Int = startColor(), // grey
        private val toColor: Int = endColor(),
        private val onAppearFinished: (() -> Unit)? = null

) : Fade() {

    override fun onAppear(sceneRoot: ViewGroup, view: View, startValues: TransitionValues, endValues: TransitionValues): Animator {
        val set = AnimatorSet()
        set.playTogether(
                super.onAppear(sceneRoot, view, startValues, endValues),
                createCircularReveal(view, true),
                animateBackgroundColor(view, fromColor, toColor)
        )
        set.addListener(onEnd = { onAppearFinished?.invoke() })
        set.duration = 350
        return set
    }

    private fun createCircularReveal(view: View, isAppearing: Boolean) : Animator{
        val cx = x
        val cy = y
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