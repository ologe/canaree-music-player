package dev.olog.ui

import android.content.Context
import android.util.AttributeSet
import android.view.ViewPropertyAnimator
import androidx.appcompat.widget.AppCompatTextView

class FadeTextView(
    context: Context,
    attrs: AttributeSet
) : AppCompatTextView(context, attrs) {

    fun updateText(text: CharSequence, textColor: Int) {
        animate().cancel()

        changeAlpha(.4f)
            .withEndAction {
                setText(text)
                setTextColor(textColor)
                changeAlpha(1f)
            }
    }

    private fun changeAlpha(alpha: Float): ViewPropertyAnimator {
        require(alpha in 0f..1f)

        return animate().alpha(alpha)
            .setDuration(75)
    }

}