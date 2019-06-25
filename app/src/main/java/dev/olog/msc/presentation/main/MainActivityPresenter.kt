package dev.olog.msc.presentation.main

import android.content.Context
import dev.olog.core.dagger.ApplicationContext
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.msc.domain.interactor.IsRepositoryEmptyUseCase
import dev.olog.shared.Permissions
import javax.inject.Inject

class MainActivityPresenter @Inject constructor(
        @ApplicationContext private val context: Context,
        private val presentationPrefs: PresentationPreferencesGateway,
        val isRepositoryEmptyUseCase: IsRepositoryEmptyUseCase
) {

    fun isFirstAccess(): Boolean {
        val canReadStorage = Permissions.canReadStorage(context)
        val isFirstAccess = presentationPrefs.isFirstAccess()
        return !canReadStorage || isFirstAccess
    }

}