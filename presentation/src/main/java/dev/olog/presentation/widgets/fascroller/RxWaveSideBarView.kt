package dev.olog.presentation.widgets.fascroller

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import dev.olog.shared.coroutines.autoDisposeJob
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.shared.TextUtils
import dev.olog.shared.android.extensions.launchWhenResumed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

class RxWaveSideBarView(
        context: Context,
        attrs: AttributeSet
) : WaveSideBarView(context, attrs) {

    var scrollableLayoutId : Int = 0

    private var job by autoDisposeJob()

    fun onDataChanged(list: List<DisplayableItem>) {
        job = launchWhenResumed {
            updateLetters(generateLetters(list))
        }
    }

    fun setListener(listener: OnTouchLetterChangeListener?){
        this.listener = listener
    }

    @SuppressLint("ConcreteDispatcherIssue")
    private suspend fun generateLetters(data: List<DisplayableItem>): List<String> = withContext(Dispatchers.Default) {
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

        yield()

        return@withContext letters
    }

    private fun updateLetters(letters: List<String>){
        this.mLetters = letters
        invalidate()
    }

}