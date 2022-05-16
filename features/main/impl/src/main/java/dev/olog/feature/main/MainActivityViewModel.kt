package dev.olog.feature.main

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.feature.main.api.MainPreferences
import dev.olog.platform.permission.Permission
import dev.olog.platform.permission.PermissionManager
import javax.inject.Inject

@HiltViewModel
internal class MainActivityViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mainPrefs: MainPreferences,
    private val permissionManager: PermissionManager,
) : ViewModel() {


    fun isFirstAccess(): Boolean {
        val canReadStorage = permissionManager.hasPermission(context, Permission.Storage)
        val isFirstAccess = mainPrefs.isFirstAccess()
        return !canReadStorage || isFirstAccess
    }

}