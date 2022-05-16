package dev.olog.feature.queue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.feature.media.api.MusicPreferencesGateway
import dev.olog.shared.TextUtils
import dev.olog.shared.extension.swap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import javax.inject.Inject

@HiltViewModel
class PlayingQueueFragmentViewModel @Inject constructor(
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    playingQueueGateway: PlayingQueueGateway,
    schedulers: Schedulers,
) : ViewModel() {

    private val moves = mutableListOf<Pair<Int, Int>>()

    private val _data = MutableStateFlow<List<PlayingQueueSong>?>(null)

    val data: Flow<List<QueueItem>> = combine(
        _data.filterNotNull(),
        musicPreferencesUseCase.observeLastIdInPlaylist().distinctUntilChanged()
    ) { queue, idInPlaylist ->
        val currentPlayingIndex = queue.indexOfFirst { it.song.idInPlaylist == idInPlaylist }
        queue.mapIndexed { index, item ->
            item.toPresentation(index, currentPlayingIndex, idInPlaylist)
        }
    }
        .flowOn(schedulers.cpu)
        .onEach { moves.clear() }

    val initialItemFlow = data
        .take(1)
        .map { list ->
            val idInPlaylist = musicPreferencesUseCase.getLastIdInPlaylist()
            list.indexOfFirst { it.idInPlaylist == idInPlaylist }
        }.filter { it >= 0 }
        .flowOn(schedulers.cpu)

    init {
        playingQueueGateway.observeAll()
            .distinctUntilChanged()
            .onEach { _data.value = it }
            .launchIn(viewModelScope)
    }

    fun recalculatePositionsAfterRemove(position: Int) {
        val currentList = _data.value.orEmpty().toMutableList()
        currentList.removeAt(position)
        _data.value = currentList
    }

    private fun PlayingQueueSong.toPresentation(
        currentPosition: Int,
        currentPlayingIndex: Int,
        currentPlayingIdInPlaylist: Int
    ): QueueItem {
        val song = this.song

        val relativePosition = computeRelativePosition(currentPosition, currentPlayingIndex)

        return QueueItem(
            mediaId = mediaId,
            title = song.title,
            subtitle = "${song.artist}${TextUtils.MIDDLE_DOT_SPACED}${song.album}",
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

    fun onMovesClear() {
        val currentList = _data.value.orEmpty()
        for ((from, to) in moves.toList()) {
            currentList.swap(from, to)
        }

        _data.value = currentList

        moves.clear()
    }

    fun recordSwap(from: Int, to: Int) {
        moves.add(from to to)
    }

}