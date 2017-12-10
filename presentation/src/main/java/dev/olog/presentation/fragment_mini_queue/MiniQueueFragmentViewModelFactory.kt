package dev.olog.presentation.fragment_mini_queue

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.domain.interactor.music_service.GetMiniPlayingQueueUseCase
import javax.inject.Inject

class MiniQueueFragmentViewModelFactory @Inject constructor(
        private val getMiniPlayingQueueUseCase: GetMiniPlayingQueueUseCase

) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MiniQueueViewModel(
              getMiniPlayingQueueUseCase) as T
    }
}