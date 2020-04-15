package dev.olog.feature.presentation.base.widget.fastscroller

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import dev.olog.core.coroutines.viewScope
import dev.olog.feature.presentation.base.model.DisplayableAlbum
import dev.olog.feature.presentation.base.model.DisplayableItem
import dev.olog.feature.presentation.base.model.DisplayableTrack
import dev.olog.shared.TextUtils
import dev.olog.shared.coroutines.autoDisposeJob
import dev.olog.shared.throwNotHandled
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

// TODO show first visibile as current?
class RxWaveSideBarView(
        context: Context,
        attrs: AttributeSet
) : WaveSideBarView(context, attrs) {

    var scrollableLayoutId : Int = 0

    private var job by autoDisposeJob()

    fun onDataChanged(list: List<DisplayableItem>) {
        job = viewScope.launchWhenAttached {
            updateLetters(generateLetters(list))
        }
    }

    fun setListener(listener: OnTouchLetterChangeListener?){
        this.listener = listener
    }

    // TODO check performance on big lists
    @SuppressLint("ConcreteDispatcherIssue")
    private suspend fun generateLetters(data: List<DisplayableItem>): List<String> = withContext(Dispatchers.Default) {
        if (scrollableLayoutId == 0){
            throw IllegalStateException("provide a real layout id to filter")
        }

        val list = data.asSequence()
            .filter { it.type == scrollableLayoutId }
            .distinctBy {
                when (it) {
                    is DisplayableTrack -> it.title.firstOrNull()
                    is DisplayableAlbum -> it.title.firstOrNull()
                    else -> throwNotHandled(it)
                }
            }
            .mapNotNull {
                when (it) {
                    is DisplayableTrack -> it.title.firstOrNull()?.toUpperCase()
                    is DisplayableAlbum -> it.title.firstOrNull()?.toUpperCase()
                    else -> throwNotHandled(it)
                }
            }
            .distinct()
            .map { it.toString() }
            .toList()

        val letters = LETTERS.map { letter -> list.firstOrNull { it == letter } ?: TextUtils.MIDDLE_DOT }
                .toMutableList()
        list.firstOrNull { it < "A" }?.let { letters[0] = "#" }
        list.firstOrNull { it > "Z" }?.let { letters[letters.lastIndex] = "?" }

        return@withContext letters
    }

    private fun updateLetters(letters: List<String>){
        this.mLetters = letters
        invalidate()
    }

}