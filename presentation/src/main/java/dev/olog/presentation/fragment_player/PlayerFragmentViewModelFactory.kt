package dev.olog.presentation.fragment_player

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.domain.interactor.favorite.IsFavoriteSongUseCase
import dev.olog.domain.interactor.favorite.ObserveFavoriteAnimationUseCase
import dev.olog.shared_android.music_service.RxMusicServiceControllerCallback
import javax.inject.Inject

class PlayerFragmentViewModelFactory @Inject constructor(
        private val application: Application,
        private val controllerCallback: RxMusicServiceControllerCallback,
        private val observeFavoriteAnimationUseCase: ObserveFavoriteAnimationUseCase,
        private val isFavoriteSongUseCase: IsFavoriteSongUseCase

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlayerFragmentViewModel(
                application, controllerCallback,
                observeFavoriteAnimationUseCase,
                isFavoriteSongUseCase
        ) as T
    }

}