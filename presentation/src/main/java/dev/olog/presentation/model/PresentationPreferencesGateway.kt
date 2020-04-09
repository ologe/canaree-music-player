package dev.olog.presentation.model

import kotlinx.coroutines.flow.Flow

internal interface PresentationPreferencesGateway {

    fun isFirstAccess(): Boolean

    fun isAdaptiveColorEnabled(): Boolean

    fun observePlayerControlsVisibility(): Flow<Boolean>


}