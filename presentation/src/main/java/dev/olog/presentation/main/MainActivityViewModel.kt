package dev.olog.presentation.main

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.shared.android.Permissions

internal class MainActivityViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val presentationPrefs: PresentationPreferencesGateway
) : ViewModel() {

    fun isFirstAccess(): Boolean {
        val canReadStorage = Permissions.canReadStorage(context)
        val isFirstAccess = presentationPrefs.isFirstAccess()
        return !canReadStorage || isFirstAccess
    }

}