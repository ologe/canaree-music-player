package dev.olog.msc.presentation.widget.image.view

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
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