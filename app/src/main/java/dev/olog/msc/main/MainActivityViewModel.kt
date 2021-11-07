package dev.olog.msc.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.feature.main.MainPrefs
import dev.olog.shared.android.permission.Permission
import dev.olog.shared.android.permission.PermissionManager
import javax.inject.Inject

@HiltViewModel
internal class MainActivityViewModel @Inject constructor(
    mainPrefs: MainPrefs,
    private val permissionManager: PermissionManager,
) : ViewModel() {

    private val firstAccessPref = mainPrefs.firstAccess

    fun isFirstAccess(): Boolean {
        val canReadStorage = permissionManager.hasPermission(Permission.Storage)
        val isFirstAccess = firstAccessPref.get().also {
            firstAccessPref.set(false)
        }
        return !canReadStorage || isFirstAccess
    }

}