package dev.olog.feature.player.api

import kotlinx.coroutines.flow.Flow

interface PlayerPreferences {

    fun observePlayerControlsVisibility(): Flow<Boolean>

    fun isAdaptiveColorEnabled(): Boolean

}