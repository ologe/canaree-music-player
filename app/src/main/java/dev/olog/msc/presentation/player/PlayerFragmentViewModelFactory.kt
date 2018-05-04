package dev.olog.msc.presentation.player

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.msc.domain.interactor.favorite.ObserveFavoriteAnimationUseCase
import dev.olog.msc.domain.interactor.prefs.MusicPreferencesUseCase
import dev.olog.msc.domain.interactor.prefs.TutorialPreferenceUseCase
import javax.inject.Inject

class PlayerFragmentViewModelFactory @Inject constructor(
        private val observeFavoriteAnimationUseCase: ObserveFavoriteAnimationUseCase,
        private val musicPrefsUseCase: MusicPreferencesUseCase,
        private val tutorialPreferenceUseCase: TutorialPreferenceUseCase

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlayerFragmentViewModel(
                observeFavoriteAnimationUseCase,
                musicPrefsUseCase,
                tutorialPreferenceUseCase
        ) as T
    }
}