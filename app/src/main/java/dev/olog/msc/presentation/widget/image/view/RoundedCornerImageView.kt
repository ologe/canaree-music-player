package dev.olog.msc.presentation.widget.image.view

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import dev.olog.msc.R
import dev.olog.msc.utils.k.extension.dip

class RoundedCornerImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0

) : AppCompatImageView(context, attrs, defStyleAttr){

    init {
        val a = context.obtainStyledAttributes(R.styleable.RoundedCornerImageView)

        val default = context.dip(5).toFloat()
        val radius = a.getDimension(R.styleable.RoundedCornerImageView_cornerRadius, default)

        val drawable = ContextCompat.getDrawable(context, R.drawable.rounded_corners_drawable) as GradientDrawable
        drawable.cornerRadius = radius
        background = drawable

        clipToOutline = true

        a.recycle()

    }

}