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

class RoundedCornerImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : ForegroundImageView(context, attrs){

    init {
        val a = context.obtainStyledAttributes(R.styleable.RoundedCornerImageView)

        val radius = a.getInt(R.styleable.RoundedCornerImageView_cornerRadius, DEFAULT_RADIUS)

        val drawable = ContextCompat.getDrawable(context, R.drawable.rounded_corners_drawable) as GradientDrawable
        drawable.cornerRadius = context.dip(radius).toFloat()
        background = drawable

        clipToOutline = true

        a.recycle()

        outlineProvider = RoundedOutlineProvider()
    }

}