package dev.olog.ui.scroller

import android.content.Context
import android.util.AttributeSet
import dev.olog.ui.model.DisplayableAlbum
import dev.olog.ui.model.DisplayableItem
import dev.olog.ui.model.DisplayableTrack
import dev.olog.shared.TextUtils

class RxWaveSideBarView(
        context: Context,
        attrs: AttributeSet
) : WaveSideBarView(context, attrs) {

    var scrollableLayoutId : Int = 0

    fun onDataChanged(list: List<DisplayableItem>){
        updateLetters(generateLetters(list))
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