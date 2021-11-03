package dev.olog.presentation.model

import dev.olog.core.Prefs
import dev.olog.feature.library.LibraryCategoryBehavior
import kotlinx.coroutines.flow.Flow

internal interface PresentationPreferencesGateway : Prefs {

    fun getLastBottomViewPage(): BottomNavigationPage
    fun setLastBottomViewPage(page: BottomNavigationPage)

    fun isFirstAccess(): Boolean

    fun isAdaptiveColorEnabled(): Boolean

    fun observePlayerControlsVisibility(): Flow<Boolean>

    fun setDefault()
}