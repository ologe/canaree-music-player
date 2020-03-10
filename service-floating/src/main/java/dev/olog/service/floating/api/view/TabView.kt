package dev.olog.service.floating.api.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.TransitionDrawable
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import dev.olog.service.floating.R
import dev.olog.shared.android.extensions.dip

private const val DURATION = 250

class TabView(
        context: Context,
        private val backgroundColors : IntArray,
        private val icon: Int

) : AppCompatImageView(context) {

    private val baseColors = intArrayOf(0xff_333333.toInt(), 0xff_333333.toInt())

    init {
        val size = context.dip(48)
        layoutParams = ViewGroup.LayoutParams(size, size)
        initialize(context)
    }

    private fun initialize(context: Context){
        setHidden(false)
        scaleType = ImageView.ScaleType.CENTER_CROP
        adjustViewBounds = true

        val padding = context.dip(14)
        setPadding(padding, padding, padding, padding)
    }

    fun setHidden(animate: Boolean){

        if (animate){
            val startGradient = ContextCompat.getDrawable(context, R.drawable.gradient)!!.mutate() as GradientDrawable
            startGradient.colors = backgroundColors
            val endGradient = ContextCompat.getDrawable(context, R.drawable.gradient)!!.mutate() as GradientDrawable
            endGradient.colors = baseColors

            val transition = TransitionDrawable(arrayOf(
                    startGradient,
                    endGradient
            ))

            background = transition
            transition.startTransition(DURATION)

        } else {
            val gradient = ContextCompat.getDrawable(context, R.drawable.gradient)!!.mutate() as GradientDrawable
            gradient.colors = baseColors
            background = gradient
        }
        setColorFilter(Color.WHITE)
        setImageResource(R.drawable.vd_bird)
    }

    fun setExpanded(){
        val startGradient = ContextCompat.getDrawable(context, R.drawable.gradient)!!.mutate() as GradientDrawable
        startGradient.colors = baseColors
        val endGradient = ContextCompat.getDrawable(context, R.drawable.gradient)!!.mutate() as GradientDrawable
        endGradient.colors = backgroundColors

        val transition = TransitionDrawable(arrayOf(
                startGradient,
                endGradient
        ))
        background = transition
        setImageResource(icon)
        setColorFilter(0xFF_262626.toInt())
        transition.startTransition(DURATION)
    }

}