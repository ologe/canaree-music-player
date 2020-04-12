package dev.olog.feature.presentation.base.activity

import dev.olog.navigation.screens.BottomNavigationPage

interface HasBottomNavigation {
    // TODO should be in :navigation??
    fun navigate(page: BottomNavigationPage)
}