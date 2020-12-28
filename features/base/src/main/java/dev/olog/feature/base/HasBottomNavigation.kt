package dev.olog.feature.base

import dev.olog.navigation.BottomNavigationPage

interface HasBottomNavigation {
    fun navigate(page: BottomNavigationPage)
}