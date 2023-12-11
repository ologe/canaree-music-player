package dev.olog.presentation.widgets.fascroller

import android.content.Context
import android.util.AttributeSet
import dev.olog.shared.TextUtils
import dev.olog.shared.android.utils.runOnMainThread

class RxWaveSideBarView(
        context: Context,
        attrs: AttributeSet
) : WaveSideBarView(context, attrs) {

    fun onLettersChanged(letters: List<String>) {
        updateLetters(mapLetters(letters))
    }

    fun setListener(listener: OnTouchLetterChangeListener?){
        this.listener = listener
    }

    private fun mapLetters(letters: List<String>): List<String> = buildList {
        for (letter in LETTERS) {
            if (letters.contains(letter)) {
                add(letter)
            } else {
                add(TextUtils.MIDDLE_DOT)
            }
            if (letters.firstOrNull { it < "A" } != null) {
                set(0, "#")
            }
            if (letters.firstOrNull { it > "Z" } != null) {
                set(lastIndex, "?")
            }
        }
    }

    private fun updateLetters(letters: List<String>){
        runOnMainThread {
            this.mLetters = letters
            invalidate()
        }
    }

}