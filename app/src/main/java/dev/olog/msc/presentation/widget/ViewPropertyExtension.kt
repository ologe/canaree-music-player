package dev.olog.msc.presentation.widget

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import dev.olog.msc.R
import dev.olog.msc.theme.AppTheme

private val colorEvaluator by lazy { ArgbEvaluator() }

fun TextView.animateTextColor(to: Int){
    animate().cancel()
    val from = currentTextColor
    computeColors(from, to) { setTextColor(it) }
}

fun View.animateBackgroundColor(to: Int){
    animate().cancel()

    val from = if (background != null && background is ColorDrawable){
        (background as ColorDrawable).color
    } else ContextCompat.getColor(context, R.color.background)
    computeColors(from, to) {setBackgroundColor(it) }
}

private fun computeColors(from: Int, to: Int, func: (Int) -> Unit){
    val animation = ValueAnimator.ofObject(colorEvaluator, from, to)
    animation.duration = 150
    animation.addUpdateListener {
        func(it.animatedValue as Int)
    }
    animation.start()
}