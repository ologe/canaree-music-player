package dev.olog.presentation.queue

import androidx.lifecycle.ViewModel
import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.entity.getMediaId
import dev.olog.core.interactor.ObservePlayingQueueUseCase
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.presentation.R

import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableQueueSong
import dev.olog.shared.extensions.asLiveData
import dev.olog.shared.extensions.debounceFirst
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject


class PlayingQueueFragmentViewModel @Inject constructor(
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    observePlayingQueueUseCase: ObservePlayingQueueUseCase

) : ViewModel() {

    fun getCurrentPosition() = musicPreferencesUseCase.getLastPositionInQueue()

    val data = Observables.combineLatest(
        observePlayingQueueUseCase.execute().debounceFirst().distinctUntilChanged(),
        musicPreferencesUseCase.observeLastPositionInQueue().distinctUntilChanged()
    ) { queue, positionInQueue ->
        queue.mapIndexed { index, item -> item.toDisplayableItem(index, positionInQueue) }
    }
        .asLiveData()

    private fun PlayingQueueSong.toDisplayableItem(
        position: Int,
        currentItemIndex: Int
    ): DisplayableQueueSong {
        val positionInList = when {
            currentItemIndex == -1 -> "-"
            position > currentItemIndex -> "+${position - currentItemIndex}"
            position < currentItemIndex -> "${position - currentItemIndex}"
            else -> "-"
        }

        return DisplayableQueueSong(
            R.layout.item_playing_queue,
            getMediaId(),
            title,
            DisplayableItem.adjustArtist(artist),
            positionInList,
            position == currentItemIndex
        )
    }
}