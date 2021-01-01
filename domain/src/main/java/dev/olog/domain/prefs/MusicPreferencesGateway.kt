package dev.olog.domain.prefs

import dev.olog.domain.ResettablePreference
import dev.olog.domain.entity.LastMetadata
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface MusicPreferencesGateway : ResettablePreference {

    var bookmark: Duration

    fun getRepeatMode(): Int
    fun setRepeatMode(repeatMode: Int)

    fun getShuffleMode(): Int
    fun setShuffleMode(shuffleMode: Int)

    fun setSkipToPreviousVisibility(visible: Boolean)
    fun observeSkipToPreviousVisibility(): Flow<Boolean>

    fun setSkipToNextVisibility(visible: Boolean)
    fun observeSkipToNextVisibility(): Flow<Boolean>

    fun isMidnightMode() : Flow<Boolean>

    fun getLastMetadata(): LastMetadata
    fun setLastMetadata(metadata: LastMetadata)
    fun observeLastMetadata(): Flow<LastMetadata>

    var crossfade: Duration
    var isGapless: Boolean

    fun observePlaybackSpeed(): Flow<Float>
    fun setPlaybackSpeed(speed: Float)
    fun getPlaybackSpeed(): Float

    var lastProgressive: Int
    fun observeLastProgressive(): Flow<Int>

    /**
     * volume from 0 to 100
     */
    var volume: Int
    fun observeVolume(): Flow<Int>

    fun observeShowLockscreenArtwork(): Flow<Boolean>

}