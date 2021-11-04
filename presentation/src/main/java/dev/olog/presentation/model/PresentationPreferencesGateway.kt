package dev.olog.presentation.model

import dev.olog.core.Prefs
import dev.olog.feature.base.bottom.nav.BottomNavigationPage

internal interface PresentationPreferencesGateway : Prefs {

    fun getLastBottomViewPage(): BottomNavigationPage
    fun setLastBottomViewPage(page: BottomNavigationPage)

    fun isFirstAccess(): Boolean

    fun setDefault()
}