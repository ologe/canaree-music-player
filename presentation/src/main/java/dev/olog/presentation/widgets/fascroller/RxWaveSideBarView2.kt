package dev.olog.presentation.widgets.fascroller

import android.content.Context
import android.util.AttributeSet
import dev.olog.shared.TextUtils

class RxWaveSideBarView2(
        context: Context,
        attrs: AttributeSet
) : WaveSideBarView(context, attrs) {

    fun <T> onDataChanged(
        list: List<T>,
        getLetter: (T) -> Char?,
    ){
        updateLetters(generateLetters(list, getLetter))
    }

    fun setListener(listener: OnTouchLetterChangeListener?){
        this.listener = listener
    }

    private fun <T> generateLetters(
        data: List<T>,
        getLetter: (T) -> Char?,
    ): List<String> {
        val list = data
                .mapNotNull { getLetter(it)?.uppercaseChar() }
                .distinctBy { it }
                .map { it.toString() }

        val letters = LETTERS.map { letter -> list.firstOrNull { it == letter } ?: TextUtils.MIDDLE_DOT }
                .toMutableList()
        list.firstOrNull { it < "A" }?.let { letters[0] = "#" }
        list.firstOrNull { it > "Z" }?.let { letters[letters.lastIndex] = "?" }
        return letters
    }

    private fun updateLetters(letters: List<String>){
        this.mLetters = letters
        invalidate()
    }

}