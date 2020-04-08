package dev.olog.feature.presentation.base.widget

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import dev.olog.feature.presentation.base.R

class CircularImageView(
    context: Context,
    attrs: AttributeSet

) : ForegroundImageView(context, attrs) {

    init {
        background = ContextCompat.getDrawable(context,
            R.drawable.circular_corners_drawable
        )
        clipToOutline = true
    }

}