package dev.olog.feature.main

interface MainPreferences {

    fun getLastBottomViewPage(): BottomNavigationPage
    fun setLastBottomViewPage(page: BottomNavigationPage)

    fun isFirstAccess(): Boolean

}