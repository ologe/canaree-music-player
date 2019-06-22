package dev.olog.presentation.interfaces

import dev.olog.presentation.main.BottomNavigationPage

interface HasBottomNavigation {
    fun navigate(page: BottomNavigationPage)
}