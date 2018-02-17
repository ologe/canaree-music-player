package dev.olog.msc.presentation.splash

import dev.olog.msc.domain.interactor.splash.PrefetchImagesUseCase
import io.reactivex.Completable
import javax.inject.Inject

class SplashActivityPresenter @Inject constructor(
        private val prefetchImagesUseCase: PrefetchImagesUseCase

) {

    fun prefetchImages(): Completable {
        // todo handle rotation change
        return prefetchImagesUseCase.execute()
    }

}