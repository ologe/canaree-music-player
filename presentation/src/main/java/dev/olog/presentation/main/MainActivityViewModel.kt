package dev.olog.presentation.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.shared.android.permission.Permission
import dev.olog.shared.android.permission.PermissionManager
import javax.inject.Inject

@HiltViewModel
internal class MainActivityViewModel @Inject constructor(
    private val presentationPrefs: PresentationPreferencesGateway,
    private val permissionManager: PermissionManager,
) : ViewModel() {


    fun isFirstAccess(): Boolean {
        val canReadStorage = permissionManager.hasPermissions(Permission.Storage)
        val isFirstAccess = presentationPrefs.isFirstAccess()
        return !canReadStorage || isFirstAccess
    }

}