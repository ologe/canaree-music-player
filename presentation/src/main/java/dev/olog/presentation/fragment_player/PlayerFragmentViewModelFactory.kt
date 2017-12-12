package dev.olog.presentation.fragment_player

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.presentation.service_music.RxMusicServiceControllerCallback
import javax.inject.Inject

class PlayerFragmentViewModelFactory @Inject constructor(
        private val application: Application,
        private val controllerCallback: RxMusicServiceControllerCallback

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlayerFragmentViewModel(
                application, controllerCallback
        ) as T
    }

}