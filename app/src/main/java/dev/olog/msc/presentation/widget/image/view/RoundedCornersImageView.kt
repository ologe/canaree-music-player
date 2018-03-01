package dev.olog.msc.presentation.widget.image.view

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import dev.olog.msc.R
import dev.olog.msc.presentation.utils.RoundedOutlineProvider
import dev.olog.msc.presentation.widget.ForegroundImageView
import dev.olog.msc.utils.k.extension.dip

private const val DEFAULT_RADIUS = 5

class RoundedCornersImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : ForegroundImageView(context, attrs) {

    private val radius : Int

    init {
        val a = context.obtainStyledAttributes(R.styleable.RoundedCornerImageView)
        radius = a.getInt(R.styleable.RoundedCornerImageView_cornerRadius, DEFAULT_RADIUS)
        a.recycle()

        val drawable = ContextCompat.getDrawable(context, R.drawable.shape_rounded_corner) as GradientDrawable
        drawable.cornerRadius = context.dip(radius).toFloat()
        background = drawable
        clipToOutline = true
        outlineProvider = RoundedOutlineProvider()
    }

}