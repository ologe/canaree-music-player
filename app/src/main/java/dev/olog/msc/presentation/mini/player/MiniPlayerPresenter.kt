package dev.olog.msc.presentation.mini.player

import dev.olog.msc.domain.interactor.prefs.MusicPreferencesUseCase
import javax.inject.Inject

class MiniPlayerPresenter @Inject constructor(
        private val musicPrefsUseCase: MusicPreferencesUseCase
) {

    fun getLastTitle() = musicPrefsUseCase.getLastTitle()
    fun getLastSubtitle() = musicPrefsUseCase.getLastSubtitle()

}