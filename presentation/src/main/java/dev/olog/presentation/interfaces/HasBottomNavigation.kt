package dev.olog.presentation.interfaces

import dev.olog.navigation.BottomNavigationPage

interface HasBottomNavigation {
    fun navigate(page: BottomNavigationPage)
}