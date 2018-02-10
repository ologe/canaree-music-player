package dev.olog.msc.presentation.player

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.msc.domain.interactor.favorite.IsFavoriteSongUseCase
import dev.olog.msc.domain.interactor.favorite.ObserveFavoriteAnimationUseCase
import dev.olog.msc.domain.interactor.music.service.GetMiniPlayingQueueUseCase
import dev.olog.shared_android.music_service.IRxMusicServiceControllerCallback
import javax.inject.Inject

class PlayerFragmentViewModelFactory @Inject constructor(
        private val application: Application,
        private val controllerCallback: IRxMusicServiceControllerCallback,
        private val observeFavoriteAnimationUseCase: ObserveFavoriteAnimationUseCase,
        private val isFavoriteSongUseCase: IsFavoriteSongUseCase,
        private val getMiniPlayingQueueUseCase: GetMiniPlayingQueueUseCase

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlayerFragmentViewModel(
                application, controllerCallback,
                observeFavoriteAnimationUseCase,
                isFavoriteSongUseCase,
                getMiniPlayingQueueUseCase
        ) as T
    }

}