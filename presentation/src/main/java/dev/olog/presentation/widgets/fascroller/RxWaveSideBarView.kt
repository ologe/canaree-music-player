package dev.olog.presentation.widgets.fascroller

import android.content.Context
import android.util.AttributeSet
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.shared.TextUtils
import dev.olog.shared.android.utils.runOnMainThread

class RxWaveSideBarView(
        context: Context,
        attrs: AttributeSet
) : WaveSideBarView(context, attrs) {

    var scrollableLayoutId : Int = 0

    fun onDataChanged(list: List<DisplayableItem>){
        updateLetters(generateLetters(list))
    }

    fun onLettersChanged(letters: List<String>) {
        updateLetters(mapLetters(letters))
    }

    fun setListener(listener: OnTouchLetterChangeListener?){
        this.listener = listener
    }

    private fun generateLetters(data: List<DisplayableItem>): List<String> {
        if (scrollableLayoutId == 0){
            throw IllegalStateException("provide a real layout id to filter")
        }

        val list = data.asSequence()
                .filter { it.type == scrollableLayoutId }
                .mapNotNull {
                    when (it) {
                        is DisplayableTrack -> it.title.firstOrNull()?.toUpperCase()
                        is DisplayableAlbum -> it.title.firstOrNull()?.toUpperCase()
                        else -> throw IllegalArgumentException("invalid type $it")
                    }
                }
                .distinctBy { it }
                .map { it.toString() }
                .toList()

        return mapLetters(list)
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