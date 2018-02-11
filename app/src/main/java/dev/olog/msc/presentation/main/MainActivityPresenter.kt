package dev.olog.msc.presentation.main

import android.app.Activity
import dev.olog.msc.domain.interactor.IsRepositoryEmptyUseCase
import dev.olog.msc.domain.interactor.prefs.AppPreferencesUseCase
import dev.olog.msc.presentation.FloatingInfoServiceHelper
import javax.inject.Inject

class MainActivityPresenter @Inject constructor(
        private val appPreferencesUseCase: AppPreferencesUseCase,
        val isRepositoryEmptyUseCase: IsRepositoryEmptyUseCase
) {

    fun isFirstAccess(hasStoragePermission: Boolean) : Boolean {
        return appPreferencesUseCase.isFirstAccess() || !hasStoragePermission
    }

    fun startFloatingService(activity: Activity){
        FloatingInfoServiceHelper.startServiceIfHasOverlayPermission(activity)
    }

}