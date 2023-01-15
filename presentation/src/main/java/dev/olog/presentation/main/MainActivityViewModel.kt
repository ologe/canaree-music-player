package dev.olog.presentation.main

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.shared.android.permission.PermissionManager
import javax.inject.Inject

@HiltViewModel
internal class MainActivityViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val presentationPrefs: PresentationPreferencesGateway,
    private val permissionManager: PermissionManager,
) : ViewModel() {


    fun isFirstAccess(): Boolean {
        val hasMandatoryPermissions = permissionManager.hasMandatoryPermissions()
        val isFirstAccess = presentationPrefs.isFirstAccess()
        return !hasMandatoryPermissions || isFirstAccess
    }

}