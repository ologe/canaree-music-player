package dev.olog.msc.presentation.main

import dev.olog.msc.domain.interactor.IsRepositoryEmptyUseCase
import dev.olog.msc.domain.interactor.prefs.AppPreferencesUseCase
import javax.inject.Inject

class MainActivityPresenter @Inject constructor(
        private val appPreferencesUseCase: AppPreferencesUseCase,
        val isRepositoryEmptyUseCase: IsRepositoryEmptyUseCase
) {

    fun isFirstAccess(hasStoragePermission: Boolean) : Boolean {
        return appPreferencesUseCase.isFirstAccess() || !hasStoragePermission
    }


}