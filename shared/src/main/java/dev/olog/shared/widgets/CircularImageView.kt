package dev.olog.shared.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import dev.olog.shared.R

class CircularImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : ForegroundImageView(context, attrs){

    init {
        background = ContextCompat.getDrawable(context, R.drawable.circular_corners_drawable)
        clipToOutline = true
    }

}