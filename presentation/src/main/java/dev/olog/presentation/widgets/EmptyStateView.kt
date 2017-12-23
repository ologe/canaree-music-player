package dev.olog.presentation.widgets

import android.content.Context
import android.content.res.ColorStateList
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import dev.olog.presentation.R
import org.jetbrains.anko.dip

class EmptyStateView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0

) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        clipChildren = false
        setImage(context, attrs)
        setDiagonalLines(context)
    }

    private lateinit var imageView: SquareImageView

    private fun setImage(context: Context, attrs: AttributeSet?){
        val a = context.theme.obtainStyledAttributes(
                attrs, R.styleable.EmptyStateView, 0, 0)

        val image = a.getDrawable(R.styleable.EmptyStateView_empty_state_src)

        imageView = SquareImageView(context)
        imageView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        )
        val padding = context.dip(10)
        imageView.setPadding(padding, padding, padding, padding)
        imageView.setImageDrawable(image)
        imageView.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.button_primary_tint))

        addView(imageView)

        a.recycle()
    }

    fun setImageResource(@DrawableRes drawableId: Int){
        imageView.setImageResource(drawableId)
    }

    private fun setDiagonalLines(context: Context){
        val linesHeight = context.dip(5)
        val linesWidth = Math.hypot(context.dip(120).toDouble(), context.dip(120).toDouble()).toInt()

        val upperWhiteLine = View(context)
        upperWhiteLine.layoutParams = FrameLayout.LayoutParams(linesWidth, linesHeight)
                .apply { this.gravity = Gravity.CENTER }
        upperWhiteLine.setBackgroundResource(R.color.background)
        upperWhiteLine.translationY = (- linesHeight).toFloat()
        upperWhiteLine.rotation = 45f

        val darkLine = View(context)
        darkLine.layoutParams = FrameLayout.LayoutParams(linesWidth, linesHeight)
                .apply { this.gravity = Gravity.CENTER }
        darkLine.setBackgroundResource(R.color.button_primary_tint)
        darkLine.rotation = 45f

        addView(upperWhiteLine)
        addView(darkLine)
    }


}