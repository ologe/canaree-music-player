package dev.olog.presentation.activity_splash

import android.animation.ArgbEvaluator
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.v4.view.ViewPager

class OnPageChangeListenerGradientBackground (
        private val viewPager: ViewPager,
        private val startColor: Int,
        private val endColors: IntArray
) : ViewPager.SimpleOnPageChangeListener() {

    private val argbEvaluator = ArgbEvaluator()
    private val gradient = GradientDrawable(GradientDrawable.Orientation.BL_TR,
            intArrayOf(Color.WHITE, Color.WHITE))

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        val offset = if (position == 0) positionOffset else 1f

        val startColor = argbEvaluator.evaluate(offset, startColor, endColors[0]) as Int
        val endColor = argbEvaluator.evaluate(offset, startColor, endColors[1]) as Int
        gradient.colors = intArrayOf(startColor, endColor)
        viewPager.background = gradient
    }

}