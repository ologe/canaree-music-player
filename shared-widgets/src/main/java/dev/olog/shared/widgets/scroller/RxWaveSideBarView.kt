package dev.olog.shared.widgets.scroller

import android.content.Context
import android.util.AttributeSet
import dev.olog.shared.TextUtils

class RxWaveSideBarView(
    context: Context,
    attrs: AttributeSet
) : WaveSideBarView(context, attrs) {

    fun onDataChanged(list: List<String>) {
        updateLetters(generateLetters(list))
    }

    fun setListener(listener: OnTouchLetterChangeListener?){
        this.listener = listener
    }

    private fun generateLetters(list: List<String>): List<String> {
        val letters = LETTERS
            .map { letter -> list.firstOrNull { it == letter } ?: TextUtils.MIDDLE_DOT }
            .toMutableList()

        list.firstOrNull { it < "A" }?.let { letters[0] = "#" }
        list.firstOrNull { it > "Z" }?.let { letters[letters.lastIndex] = "?" }

        return letters
    }

    private fun updateLetters(letters: List<String>) {
        mLetters = letters
        invalidate()
    }

}