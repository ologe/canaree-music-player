package dev.olog.presentation.queue

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableQueueSong
import dev.olog.shared.swap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class PlayingQueueFragmentViewModel @ViewModelInject constructor(
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    playingQueueGateway: PlayingQueueGateway

) : ViewModel() {

    val lastProgressive: Int
        get() =  musicPreferencesUseCase.lastProgressive

    private val dataPublisher = MutableStateFlow<List<DisplayableQueueSong>>(emptyList())
    private val queuePublisher = MutableStateFlow<List<PlayingQueueSong>>(emptyList())

    init {
        playingQueueGateway.observeAll()
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)
            .onEach { queuePublisher.value = it }
            .launchIn(viewModelScope)

        queuePublisher.combine(musicPreferencesUseCase.observeLastProgressive().distinctUntilChanged())
        { queue, progressive ->
            val currentPlayingIndex = queue.indexOfFirst { it.song.idInPlaylist == progressive }
            queue.mapIndexed { index, item ->
                item.toDisplayableItem(
                    currentPosition = index,
                    currentPlayingIndex = currentPlayingIndex,
                    currentPlayingIdInPlaylist = progressive
                )
            }
        }
            .flowOn(Dispatchers.Default)
            .onEach { dataPublisher.value = it }
            .launchIn(viewModelScope)
    }

    fun observeData(): Flow<List<DisplayableQueueSong>> = dataPublisher

    fun recalculatePositionsAfterRemove(position: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            val currentList = queuePublisher.value.toMutableList()
            currentList.removeAt(position)

            queuePublisher.value = currentList
        }
    }

    /**
     * @param moves contains all the movements in the list
     */
    fun recalculatePositionsAfterMove(moves: List<Pair<Int, Int>>) {
        viewModelScope.launch(Dispatchers.Default) {
            val currentList = queuePublisher.value.toMutableList()
            for ((from, to) in moves) {
                currentList.swap(from, to)
            }

            queuePublisher.value = currentList
        }
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