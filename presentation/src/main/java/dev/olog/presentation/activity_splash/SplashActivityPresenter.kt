package dev.olog.presentation.activity_splash

import dev.olog.domain.interactor.splash.FirstAccessUseCase
import javax.inject.Inject

class SplashActivityPresenter @Inject constructor(
        private val firstAccessUseCase : FirstAccessUseCase
) {

    fun isFirstAccess(hasStoragePermission: Boolean): Boolean {
        val isFirstAccess = firstAccessUseCase.get()
        return isFirstAccess || !hasStoragePermission
    }



}