package dev.olog.core.prefs

import kotlinx.coroutines.flow.Flow

interface MusicPreferencesGateway {

    fun getBookmark(): Long
    fun setBookmark(bookmark: Long)

    fun getRepeatMode(): Int
    fun setRepeatMode(repeatMode: Int)

    fun getShuffleMode(): Int
    fun setShuffleMode(shuffleMode: Int)

    fun setSkipToPreviousVisibility(visible: Boolean)
    fun observeSkipToPreviousVisibility(): Flow<Boolean>

    fun setSkipToNextVisibility(visible: Boolean)
    fun observeSkipToNextVisibility(): Flow<Boolean>

    fun isMidnightMode() : Flow<Boolean>

    fun setDefault()

    /**
     * in millis
     */
    fun observeCrossFade(): Flow<Int>
    fun observeGapless(): Flow<Boolean>

    fun observePlaybackSpeed(): Flow<Float>
    fun setPlaybackSpeed(speed: Float)
    fun getPlaybackSpeed(): Float

    fun setLastIdInPlaylist(position: Int)
    fun observeLastIdInPlaylist(): Flow<Int>
    fun getLastIdInPlaylist(): Int

    /**
     * volume from 0 to 100
     */
    fun setVolume(volume: Int)
    fun getVolume(): Int
    fun observeVolume(): Flow<Int>

    fun observeShowLockscreenArtwork(): Flow<Boolean>

}