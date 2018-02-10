package dev.olog.msc.presentation.playing.queue

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.msc.domain.interactor.music.service.CurrentIdInPlaylistUseCase
import dev.olog.msc.domain.interactor.music.service.ObserveCurrentSongId
import dev.olog.msc.domain.interactor.music.service.ObservePlayingQueueUseCase
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