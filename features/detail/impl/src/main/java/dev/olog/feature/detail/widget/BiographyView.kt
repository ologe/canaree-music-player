package dev.olog.feature.detail.widget

import android.content.Context
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView
import kotlin.properties.Delegates

internal class BiographyView(
    context: Context,
    attrs: AttributeSet
) : AppCompatTextView(context, attrs) {

    companion object {
        private const val MIN_LINES = 2
    }

    private var isExpanded by Delegates.observable(false) { _, _, new ->
        if (new){
            maxLines = Int.MAX_VALUE
        } else {
            maxLines = MIN_LINES
        }
    }

    init {
        maxLines = MIN_LINES
        ellipsize = TextUtils.TruncateAt.END
        isClickable = true
        isFocusable = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_UP && !isExpanded) {
            isExpanded = true
            movementMethod = LinkMovementMethod.getInstance()
            return true
        }
        return super.onTouchEvent(event)
    }

}