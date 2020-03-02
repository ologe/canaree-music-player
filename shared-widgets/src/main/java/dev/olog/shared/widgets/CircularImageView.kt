package dev.olog.shared.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat

class CircularImageView(
    context: Context,
    attrs: AttributeSet

) : ForegroundImageView(context, attrs) {

    init {
        background = ContextCompat.getDrawable(context, R.drawable.circular_corners_drawable)
        clipToOutline = true
    }

}