package dev.olog.presentation.model

import dev.olog.navigation.screens.BottomNavigationPage
import kotlinx.coroutines.flow.Flow

internal interface PresentationPreferencesGateway {

    fun getLastBottomViewPage(): BottomNavigationPage
    fun setLastBottomViewPage(page: BottomNavigationPage)



    fun isFirstAccess(): Boolean

    fun isAdaptiveColorEnabled(): Boolean

    fun observePlayerControlsVisibility(): Flow<Boolean>


}