package dev.olog.presentation.main

import android.content.Context
import androidx.lifecycle.ViewModel
import dev.olog.shared.ApplicationContext
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.shared.android.Permissions
import javax.inject.Inject

internal class MainActivityViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val presentationPrefs: PresentationPreferencesGateway
) : ViewModel() {


    fun isFirstAccess(): Boolean {
        val canReadStorage = Permissions.canReadStorage(context)
        val isFirstAccess = presentationPrefs.isFirstAccess()
        return !canReadStorage || isFirstAccess
    }

}