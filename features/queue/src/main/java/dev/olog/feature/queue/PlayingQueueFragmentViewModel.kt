package dev.olog.feature.queue

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.entity.PlayingQueueTrack
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.shared.android.DisplayableItemUtils
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

    private val dataPublisher = MutableStateFlow<List<PlayingQueueFragmentModel>>(emptyList())
    private val queuePublisher = MutableStateFlow<List<PlayingQueueTrack>>(emptyList())

    init {
        playingQueueGateway.observeAll()
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)
            .onEach { queuePublisher.value = it }
            .launchIn(viewModelScope)

        queuePublisher.combine(musicPreferencesUseCase.observeLastProgressive().distinctUntilChanged())
        { queue, progressive ->
            val indexInThisQueue = queue.indexOfFirst { it.serviceProgressive == progressive }
            queue.mapIndexed { index, item ->
                item.toDisplayableItem(
                    trackIndexInThisQueue = index,
                    playingIndexInThisQueue = indexInThisQueue,
                    playingIndexInServiceQueue = progressive
                )
            }
        }
            .flowOn(Dispatchers.Default)
            .onEach { dataPublisher.value = it }
            .launchIn(viewModelScope)
    }

    fun observeData(): Flow<List<PlayingQueueFragmentModel>> = dataPublisher

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

    private fun PlayingQueueTrack.toDisplayableItem(
        trackIndexInThisQueue: Int,
        playingIndexInThisQueue: Int,
        playingIndexInServiceQueue: Int
    ): PlayingQueueFragmentModel {
        val track = this.track

        val relativePosition = when {
            trackIndexInThisQueue > playingIndexInThisQueue -> "+${trackIndexInThisQueue - playingIndexInThisQueue}"
            trackIndexInThisQueue < playingIndexInThisQueue -> "${trackIndexInThisQueue - playingIndexInThisQueue}"
            else -> "-"
        }

        return PlayingQueueFragmentModel(
            progressive = serviceProgressive,
            mediaId = track.getMediaId(),
            title = track.title,
            subtitle = DisplayableItemUtils.trackSubtitle(track.artist, track.album),
            relativePosition = relativePosition,
            isCurrentSong = serviceProgressive == playingIndexInServiceQueue
        )
    }

}