package dev.olog.msc.presentation.playing.queue

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.msc.domain.interactor.playing.queue.ObservePlayingQueueUseCase
import dev.olog.msc.domain.interactor.prefs.MusicPreferencesUseCase
import javax.inject.Inject

class PlayingQueueFragmentViewModelFactory @Inject constructor(
        private val musicPreferencesUseCase: MusicPreferencesUseCase,
        private val observePlayingQueueUseCase: ObservePlayingQueueUseCase

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlayingQueueFragmentViewModel(
                musicPreferencesUseCase,
                observePlayingQueueUseCase
        ) as T
    }
}