package dev.olog.presentation.main

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.feature.presentation.base.prefs.CommonPreferences
import dev.olog.navigation.Navigator
import dev.olog.navigation.screens.BottomNavigationPage
import dev.olog.navigation.screens.FragmentScreen
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.shared.android.Permissions

internal class MainActivityViewModel2 @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val presentationPrefs: PresentationPreferencesGateway,
    private val navigator: Navigator,
    private val prefs: CommonPreferences
) : ViewModel() {

    fun isFirstAccess(): Boolean {
        val canReadStorage = Permissions.canReadStorage(context)
        val isFirstAccess = presentationPrefs.isFirstAccess()
        return !canReadStorage || isFirstAccess
    }

    val lastBottomNavigationPage: BottomNavigationPage
        get() = prefs.lastBottomNavigationPage

    fun onPageChanged(page: BottomNavigationPage = lastBottomNavigationPage) {
        prefs.lastBottomNavigationPage = page
        navigator.bottomNavigate(page.toScreen())
    }

    private fun BottomNavigationPage.toScreen(): FragmentScreen {
        return when (this){
            BottomNavigationPage.HOME -> FragmentScreen.HOME
            BottomNavigationPage.LIBRARY -> FragmentScreen.LIBRARY
            BottomNavigationPage.SEARCH -> FragmentScreen.SEARCH
            BottomNavigationPage.PLAYLIST -> FragmentScreen.PLAYLIST
            BottomNavigationPage.QUEUE -> FragmentScreen.QUEUE
        }
    }

}