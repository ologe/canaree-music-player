package dev.olog.presentation.queue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableQueueSong
import dev.olog.shared.extensions.assertBackground
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combineLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject


class PlayingQueueFragmentViewModel @Inject constructor(
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    playingQueueGateway: PlayingQueueGateway

) : ViewModel() {

    fun getCurrentPosition() = musicPreferencesUseCase.getLastPositionInQueue()

    private val data = MutableLiveData<List<DisplayableQueueSong>>()

    init {
        viewModelScope.launch {
            playingQueueGateway.observeAll().distinctUntilChanged()
                .combineLatest(musicPreferencesUseCase.observeLastPositionInQueue().distinctUntilChanged())
                { queue, positionInQueue ->
                    queue.map { item ->
                        item.toDisplayableItem(positionInQueue)
                    }
                }
                .assertBackground()
                .flowOn(Dispatchers.Default)
                .collect {
                    data.value = it
                }
        }
    }

    fun observeData(): LiveData<List<DisplayableQueueSong>> = data

    private fun PlayingQueueSong.toDisplayableItem(
        currentItemIndex: Int
    ): DisplayableQueueSong {
        val song = this.song

        val relativePosition = when {
//            currentItemIndex == -1 -> "-"
            song.idInPlaylist > currentItemIndex -> "+${song.idInPlaylist - currentItemIndex}"
            song.idInPlaylist < currentItemIndex -> "${song.idInPlaylist - currentItemIndex}"
            else -> "-"
        }

        return DisplayableQueueSong(
            type = R.layout.item_playing_queue,
            mediaId = mediaId,
            title = song.title,
            artist = song.artist,
            album = song.album,
            idInPlaylist = song.idInPlaylist,
            relativePosition = relativePosition,
            isCurrentSong = song.idInPlaylist == currentItemIndex
        )
    }
}