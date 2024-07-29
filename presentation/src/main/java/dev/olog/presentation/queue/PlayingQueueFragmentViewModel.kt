package dev.olog.presentation.queue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.core.interactor.UpdatePlayingQueueUseCaseRequest
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.shared.android.TextUtils
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
    private val playingQueueGateway: PlayingQueueGateway,
) : ViewModel() {

    fun getLastIdInPlaylist() = musicPreferencesUseCase.getLastIdInPlaylist()

    private val data = MutableStateFlow<List<QueueItem>?>(null)

    init {
        viewModelScope.launch {
            playingQueueGateway.observeAll().distinctUntilChanged()
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

    fun observeData(): Flow<List<QueueItem>> = data.filterNotNull()

    fun updateQueue(list: List<QueueItem>) {
        viewModelScope.launch {
            playingQueueGateway.update(
                list.map {
                    UpdatePlayingQueueUseCaseRequest(
                        mediaId = it.mediaId,
                        songId = it.id,
                        idInPlaylist = it.idInPlaylist,
                    )
                }
            )
        }
    }

    private fun PlayingQueueSong.toDisplayableItem(
        currentPosition: Int,
        currentPlayingIndex: Int,
        currentPlayingIdInPlaylist: Int
    ): QueueItem {
        val song = this.song

        val relativePosition = computeRelativePosition(currentPosition, currentPlayingIndex)

        return QueueItem(
            id = song.id,
            mediaId = mediaId,
            title = song.title,
            subtitle = TextUtils.subtitle(song.artist, song.album),
            idInPlaylist = song.idInPlaylist,
            relativePosition = relativePosition,
            isPlaying = song.idInPlaylist == currentPlayingIdInPlaylist,
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