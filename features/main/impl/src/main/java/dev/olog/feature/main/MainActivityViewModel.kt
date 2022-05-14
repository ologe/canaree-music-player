package dev.olog.feature.main

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.feature.main.api.MainPreferences
import dev.olog.platform.permission.Permissions
import javax.inject.Inject

@HiltViewModel
internal class MainActivityViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mainPrefs: MainPreferences
) : ViewModel() {


    fun isFirstAccess(): Boolean {
        val canReadStorage = Permissions.canReadStorage(context)
        val isFirstAccess = mainPrefs.isFirstAccess()
        return !canReadStorage || isFirstAccess
    }

}