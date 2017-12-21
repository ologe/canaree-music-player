package dev.olog.presentation.activity_splash

import dev.olog.domain.interactor.splash.FirstAccessUseCase
import dev.olog.domain.interactor.splash.PrefetchImagesUseCase
import io.reactivex.Completable
import javax.inject.Inject

class SplashActivityPresenter @Inject constructor(
        private val firstAccessUseCase : FirstAccessUseCase,
        private val prefetchImagesUseCase: PrefetchImagesUseCase

) {

    fun isFirstAccess(hasStoragePermission: Boolean): Boolean {
        val isFirstAccess = firstAccessUseCase.get()
        return isFirstAccess || !hasStoragePermission
    }

    fun prefetchImages(): Completable {
        return prefetchImagesUseCase.execute()
    }

}