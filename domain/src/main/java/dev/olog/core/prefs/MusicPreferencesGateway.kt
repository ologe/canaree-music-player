package dev.olog.core.prefs

import dev.olog.core.entity.LastMetadata
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface MusicPreferencesGateway {

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

    fun setDefault()

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