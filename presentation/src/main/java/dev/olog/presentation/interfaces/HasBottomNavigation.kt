package dev.olog.presentation.interfaces

import dev.olog.presentation.model.BottomNavigationPage

interface HasBottomNavigation {
    fun navigate(page: BottomNavigationPage)
}