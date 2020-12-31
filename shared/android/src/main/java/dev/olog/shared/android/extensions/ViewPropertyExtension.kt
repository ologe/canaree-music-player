package dev.olog.shared.android.extensions

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton

private val colorEvaluator by lazy { ArgbEvaluator() }

fun TextView.animateTextColor(to: Int) {
    animate().cancel()
    val from = currentTextColor
    computeColors(from, to) { setTextColor(it) }
}

fun FloatingActionButton.animateBackgroundColor(to: Int) {
    animate().cancel()
    val from = backgroundTintList!!.defaultColor
    computeColors(from, to) {
        backgroundTintList = ColorStateList.valueOf(it)
    }
}

fun View.animateBackgroundColor(to: Int) {
    animate().cancel()

    val from = if (background != null && background is ColorDrawable) {
        (background as ColorDrawable).color
    } else context.colorSurface()
    computeColors(from, to) { setBackgroundColor(it) }
}

private fun computeColors(from: Int, to: Int, func: (Int) -> Unit) {
    val animation = ValueAnimator.ofObject(colorEvaluator, from, to)
    animation.duration = 150
    animation.addUpdateListener {
        func(it.animatedValue as Int)
    }
    animation.start()
}