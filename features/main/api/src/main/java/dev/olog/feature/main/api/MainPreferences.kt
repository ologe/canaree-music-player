package dev.olog.feature.main.api

interface MainPreferences {

    fun getLastBottomViewPage(): BottomNavigationPage
    fun setLastBottomViewPage(page: BottomNavigationPage)

    fun isFirstAccess(): Boolean

}