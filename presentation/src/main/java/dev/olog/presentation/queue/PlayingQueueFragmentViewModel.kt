package dev.olog.presentation.queue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.presentation.PresentationId
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableQueueSong
import dev.olog.presentation.toPresentation
import dev.olog.shared.swap
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlayingQueueFragmentViewModel @Inject constructor(
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    playingQueueGateway: PlayingQueueGateway,
    private val schedulers: Schedulers
) : ViewModel() {

    fun getLastIdInPlaylist() = musicPreferencesUseCase.getLastIdInPlaylist()

    private val data = ConflatedBroadcastChannel<List<DisplayableQueueSong>>()

    private val queuePublisher = ConflatedBroadcastChannel<List<PlayingQueueSong>>()

    init {
        playingQueueGateway.observeAll().distinctUntilChanged()
            .flowOn(schedulers.cpu)
            .onEach { queuePublisher.offer(it) }
            .launchIn(viewModelScope)

        queuePublisher.asFlow()
            .combine(musicPreferencesUseCase.observeLastIdInPlaylist().distinctUntilChanged())
            { queue, idInPlaylist ->
                val currentPlayingIndex =
                    queue.indexOfFirst { it.song.idInPlaylist == idInPlaylist }
                queue.mapIndexed { index, item ->
                    item.toDisplayableItem(index, currentPlayingIndex, idInPlaylist)
                }
            }
            .flowOn(schedulers.cpu)
            .onEach { data.offer(it)  }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        queuePublisher.close()
        data.close()
    }

    fun observeData(): Flow<List<DisplayableQueueSong>> = data.asFlow()

    fun recalculatePositionsAfterRemove(
        position: Int
    ) = viewModelScope.launch(schedulers.cpu) {
        val currentList = queuePublisher.value.toMutableList()
        currentList.removeAt(position)

        queuePublisher.offer(currentList)
    }

    /**
     * @param moves contains all the movements in the list
     */
    fun recalculatePositionsAfterMove(
        moves: List<Pair<Int, Int>>
    ) = viewModelScope.launch(schedulers.cpu) {
        val currentList = queuePublisher.value
        for ((from, to) in moves) {
            currentList.swap(from, to)
        }

        queuePublisher.offer(currentList)
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
            mediaId = mediaId.toPresentation(),
            title = song.title,
            artist = song.artist,
            album = song.album,
            idInPlaylist = song.idInPlaylist,
            relativePosition = relativePosition,
            isCurrentSong = song.idInPlaylist == currentPlayingIdInPlaylist
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