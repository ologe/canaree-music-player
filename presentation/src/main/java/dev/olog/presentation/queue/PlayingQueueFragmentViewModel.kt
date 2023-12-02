package dev.olog.presentation.queue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.shared.swap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayingQueueFragmentViewModel @Inject constructor(
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    playingQueueGateway: PlayingQueueGateway

) : ViewModel() {

    fun getLastIdInPlaylist() = musicPreferencesUseCase.getLastIdInPlaylist()

    private val data = MutableLiveData<List<PlayingQueueFragmentItem>>()

    private val queueLiveData = MutableStateFlow<List<PlayingQueueSong>?>(null)

    init {
        viewModelScope.launch {
            playingQueueGateway.observeAll().distinctUntilChanged()
                .flowOn(Dispatchers.Default)
                .collect { queueLiveData.value = it }
        }

        viewModelScope.launch {
            queueLiveData.filterNotNull()
                .combine(musicPreferencesUseCase.observeLastIdInPlaylist().distinctUntilChanged())
                { queue, idInPlaylist ->
                    val currentPlayingIndex = queue.indexOfFirst { it.song.idInPlaylist == idInPlaylist }
                    queue.mapIndexed { index, item ->
                        item.toDisplayableItem(index, currentPlayingIndex, idInPlaylist)
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect {
                    data.value = it
                }
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }

    fun observeData(): LiveData<List<PlayingQueueFragmentItem>> = data

    fun recalculatePositionsAfterRemove(position: Int) {
        val currentList = queueLiveData.value?.toMutableList() ?: return
        currentList.removeAt(position)
        queueLiveData.value = currentList
    }

    /**
     * @param moves contains all the movements in the list
     */
    fun recalculatePositionsAfterMove(moves: List<Pair<Int, Int>>) {
        val currentList = queueLiveData.value ?: return
        for ((from, to) in moves) {
            currentList.swap(from, to)
        }
        queueLiveData.value = currentList
    }

    private fun PlayingQueueSong.toDisplayableItem(
        currentPosition: Int,
        currentPlayingIndex: Int,
        currentPlayingIdInPlaylist: Int
    ): PlayingQueueFragmentItem {
        val song = this.song

        val relativePosition = computeRelativePosition(currentPosition, currentPlayingIndex)

        return PlayingQueueFragmentItem(
            mediaId = mediaId,
            title = song.title,
            subtitle = DisplayableTrack.subtitle(song.artist, song.album),
            idInPlaylist = song.idInPlaylist,
            relativePosition = relativePosition,
            isCurrentlyPlaying = song.idInPlaylist == currentPlayingIdInPlaylist
        )
    }

    private fun computeRelativePosition(
        currentPosition: Int,
        currentPlayingIndex: Int
    ): String {
        return when {
            currentPosition > currentPlayingIndex -> "+${currentPosition - currentPlayingIndex}"
            currentPosition < currentPlayingIndex -> "${currentPosition - currentPlayingIndex}"
            else -> "-"
        }
    }
}