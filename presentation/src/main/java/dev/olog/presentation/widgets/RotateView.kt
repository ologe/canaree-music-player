package dev.olog.presentation.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.withStyledAttributes
import dev.olog.presentation.R
import java.lang.ref.WeakReference

class RotateView(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageButton(context, attrs) {

    companion object {
        private const val DEFAULT = 30
    }

    private var degrees: Float = 0f

    init {
        context.withStyledAttributes(attrs, R.styleable.RotateView) {
            degrees = getInteger(R.styleable.RotateView_direction, DEFAULT).toFloat()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        animateMe()
        return super.onTouchEvent(event)
    }

    private fun animateMe() {
        val weak = WeakReference(this)
        animate().cancel()
        animate().rotation(degrees)
            .setDuration(200)
            .withEndAction { weak.get()?.animate()?.rotation(0f)?.setDuration(200) }
    }

}