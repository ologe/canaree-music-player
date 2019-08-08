package dev.olog.presentation.main

import android.content.Context
import androidx.lifecycle.ViewModel
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.prefs.AppPreferencesGateway
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.shared.android.Permissions
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefsGateway: AppPreferencesGateway,
    private val presentationPrefs: PresentationPreferencesGateway
) : ViewModel() {


    fun isFirstAccess(): Boolean {
        val canReadStorage = Permissions.canReadStorage(context)
        val isFirstAccess = presentationPrefs.isFirstAccess()
        return !canReadStorage || isFirstAccess
    }

    fun canShowAds(): Boolean {
        return prefsGateway.canShowAds()
    }

}