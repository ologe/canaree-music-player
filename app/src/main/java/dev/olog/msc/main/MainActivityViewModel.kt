package dev.olog.msc.main

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.feature.main.MainPrefs
import dev.olog.shared.android.Permissions
import javax.inject.Inject

@HiltViewModel
internal class MainActivityViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    mainPrefs: MainPrefs,
) : ViewModel() {

    private val firstAccessPref = mainPrefs.firstAccess

    fun isFirstAccess(): Boolean {
        val canReadStorage = Permissions.canReadStorage(context)
        val isFirstAccess = firstAccessPref.get().also {
            firstAccessPref.set(false)
        }
        return !canReadStorage || isFirstAccess
    }

}