package dev.olog.presentation.queue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.feature.media.MusicPreferencesGateway
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableQueueSong
import dev.olog.shared.extension.swap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayingQueueFragmentViewModel @Inject constructor(
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    playingQueueGateway: PlayingQueueGateway

) : ViewModel() {

    fun getLastIdInPlaylist() = musicPreferencesUseCase.getLastIdInPlaylist()

    private val queueLiveData = MutableStateFlow<List<PlayingQueueSong>?>(null)

    val data: Flow<List<DisplayableQueueSong>> = combine(
        queueLiveData.filterNotNull(),
        musicPreferencesUseCase.observeLastIdInPlaylist().distinctUntilChanged()
    ) { queue, idInPlaylist ->
        val currentPlayingIndex = queue.indexOfFirst { it.song.idInPlaylist == idInPlaylist }
        queue.mapIndexed { index, item ->
            item.toDisplayableItem(index, currentPlayingIndex, idInPlaylist)
        }
    }

    init {
        viewModelScope.launch {
            playingQueueGateway.observeAll().distinctUntilChanged()
                .flowOn(Dispatchers.Default)
                .collect { queueLiveData.value = it }
        }
    }

    fun observeData(): Flow<List<DisplayableQueueSong>> = data

    fun recalculatePositionsAfterRemove(position: Int) =
        viewModelScope.launch(Dispatchers.Default) {
            val currentList = queueLiveData.value.orEmpty().toMutableList()
            currentList.removeAt(position)

            queueLiveData.value = currentList
        }

    /**
     * @param moves contains all the movements in the list
     */
    fun recalculatePositionsAfterMove(moves: List<Pair<Int, Int>>) =
        viewModelScope.launch(Dispatchers.Default) {
            val currentList = queueLiveData.value.orEmpty()
            for ((from, to) in moves) {
                currentList.swap(from, to)
            }

            queueLiveData.value = currentList
        }

    private fun PlayingQueueSong.toDisplayableItem(
        currentPosition: Int,
        currentPlayingIndex: Int,
        currentPlayingIdInPlaylist: Int
    ): DisplayableQueueSong {
        val song = this.song

        val relativePosition = computeRelativePosition(currentPosition, currentPlayingIndex)

        return DisplayableQueueSong(
            type = R.layout.item_playing_queue,
            mediaId = mediaId,
            title = song.title,
            artist = song.artist,
            album = song.album,
            idInPlaylist = song.idInPlaylist,
            relativePosition = relativePosition,
            isCurrentSong = song.idInPlaylist == currentPlayingIdInPlaylist
        )
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun computeRelativePosition(
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