package dev.olog.msc.presentation.widget.image.view

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import dev.olog.msc.R

class CircularImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : ForegroundImageView(context, attrs){

    init {
        background = ContextCompat.getDrawable(context, R.drawable.circular_corners_drawable)
        clipToOutline = true
    }

}