package dev.olog.msc.presentation.mini.player

import dev.olog.msc.domain.interactor.prefs.MusicPreferencesUseCase
import dev.olog.msc.utils.k.extension.asLiveData
import javax.inject.Inject

class MiniPlayerFragmentPresenter @Inject constructor(
        private val musicPrefsUseCase: MusicPreferencesUseCase

)  {

    val skipToNextVisibility = musicPrefsUseCase
            .observeSkipToNextVisibility()
            .asLiveData()

    val skipToPreviousVisibility = musicPrefsUseCase
            .observeSkipToPreviousVisibility()
            .asLiveData()

    fun getMetadata() = musicPrefsUseCase.getLastMetadata()

}