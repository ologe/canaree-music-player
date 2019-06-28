package dev.olog.presentation.equalizer

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

class ResizeAnimation(
        private val view: View,
        private val targetHeight: Int

) : Animation() {

    private val startHeight: Int = view.height

    override fun willChangeBounds(): Boolean = true

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        val newHeight = startHeight + targetHeight * interpolatedTime
        view.layoutParams.height = newHeight.toInt()
        view.requestLayout()
    }

}