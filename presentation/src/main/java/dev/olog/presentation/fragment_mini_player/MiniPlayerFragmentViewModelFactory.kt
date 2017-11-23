package dev.olog.presentation.fragment_mini_player

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.presentation.music_service.RxMusicServiceControllerCallback
import javax.inject.Inject

class MiniPlayerFragmentViewModelFactory @Inject constructor(
        private val controllerCallback: RxMusicServiceControllerCallback

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MiniPlayerFragmentViewModel(controllerCallback) as T
    }

}
