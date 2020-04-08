package dev.olog.feature.presentation.base.widget.autoscroll

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

open class AutoScrollTextView(
    context: Context,
    attrs: AttributeSet
) : AppCompatTextView(context, attrs) {

    init {
        ellipsize = TextUtils.TruncateAt.MARQUEE
        marqueeRepeatLimit = -1
        isSingleLine = true
        isSelected = true
        isHorizontalFadingEdgeEnabled = true
    }

}