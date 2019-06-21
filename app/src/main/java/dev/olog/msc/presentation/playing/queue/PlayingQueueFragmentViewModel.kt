package dev.olog.msc.presentation.playing.queue

import androidx.lifecycle.ViewModel
import dev.olog.msc.R
import dev.olog.msc.domain.entity.PlayingQueueSong
import dev.olog.msc.domain.gateway.prefs.MusicPreferencesGateway
import dev.olog.msc.domain.interactor.playing.queue.ObservePlayingQueueUseCase
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.playing.queue.model.DisplayableQueueSong
import dev.olog.core.MediaId
import dev.olog.msc.utils.k.extension.asLiveData
import dev.olog.msc.utils.k.extension.debounceFirst
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

    private fun PlayingQueueSong.toDisplayableItem(position: Int, currentItemIndex: Int): DisplayableQueueSong {
        val positionInList = when {
            currentItemIndex == -1 -> "-"
            position > currentItemIndex -> "+${position - currentItemIndex}"
            position < currentItemIndex -> "${position - currentItemIndex}"
            else -> "-"
        }

        return DisplayableQueueSong(
                R.layout.item_playing_queue,
                MediaId.songId(this.idInPlaylist.toLong()),
                title,
                DisplayableItem.adjustArtist(artist),
                image,
                positionInList,
                position == currentItemIndex
        )
    }
}