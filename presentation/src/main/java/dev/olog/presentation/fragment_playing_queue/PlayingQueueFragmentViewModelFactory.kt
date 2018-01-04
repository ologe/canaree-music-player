package dev.olog.presentation.fragment_playing_queue

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.domain.interactor.music_service.CurrentIdInPlaylistUseCase
import dev.olog.domain.interactor.music_service.ObserveCurrentSongId
import dev.olog.domain.interactor.music_service.ObservePlayingQueueUseCase
import javax.inject.Inject

class PlayingQueueFragmentViewModelFactory @Inject constructor(
        private val observePlayingQueueUseCase: ObservePlayingQueueUseCase,
        private val currentSongIdUseCase: CurrentIdInPlaylistUseCase,
        private val observeCurrentSongId: ObserveCurrentSongId

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlayingQueueFragmentViewModel(
                observePlayingQueueUseCase,
                currentSongIdUseCase,
                observeCurrentSongId
        ) as T
    }
}