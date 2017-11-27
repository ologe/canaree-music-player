package dev.olog.presentation.fragment_queue

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.domain.interactor.service.GetMiniPlayingQueueUseCase
import javax.inject.Inject

class PlayingQueueViewModelFactory @Inject constructor(
        private val getMiniPlayingQueueUseCase: GetMiniPlayingQueueUseCase

) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlayingQueueViewModel(
              getMiniPlayingQueueUseCase) as T
    }
}