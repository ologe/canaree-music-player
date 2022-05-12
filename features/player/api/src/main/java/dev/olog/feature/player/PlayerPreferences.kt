package dev.olog.feature.player

import kotlinx.coroutines.flow.Flow

interface PlayerPreferences {

    fun observePlayerControlsVisibility(): Flow<Boolean>

    fun isAdaptiveColorEnabled(): Boolean

}