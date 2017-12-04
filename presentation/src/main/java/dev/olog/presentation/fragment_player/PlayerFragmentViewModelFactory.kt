package dev.olog.presentation.fragment_player

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.presentation.service_music.RxMusicServiceControllerCallback
import javax.inject.Inject

class PlayerFragmentViewModelFactory @Inject constructor(
        private val controllerCallback: RxMusicServiceControllerCallback
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlayerFragmentViewModel(controllerCallback) as T
    }

}