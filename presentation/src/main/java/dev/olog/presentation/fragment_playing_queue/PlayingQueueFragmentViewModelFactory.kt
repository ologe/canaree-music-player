package dev.olog.presentation.fragment_playing_queue

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.domain.interactor.music_service.ObservePlayingQueueUseCase
import dev.olog.presentation.service_music.RxMusicServiceControllerCallback
import javax.inject.Inject

class PlayingQueueFragmentViewModelFactory @Inject constructor(
        private val observePlayingQueueUseCase: ObservePlayingQueueUseCase,
        private val controllerCallback: RxMusicServiceControllerCallback

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlayingQueueFragmentViewModel(
                observePlayingQueueUseCase, controllerCallback
        ) as T
    }
}