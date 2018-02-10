package dev.olog.msc.presentation.splash

import dev.olog.msc.domain.interactor.splash.FirstAccessUseCase
import dev.olog.msc.domain.interactor.splash.PrefetchImagesUseCase
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