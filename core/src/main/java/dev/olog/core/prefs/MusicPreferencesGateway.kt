package dev.olog.core.prefs

import dev.olog.core.entity.LastMetadata
import io.reactivex.Completable
import io.reactivex.Observable

interface MusicPreferencesGateway {

    fun getBookmark(): Long
    fun setBookmark(bookmark: Long)

    fun getLastIdInPlaylist(): Int
    fun setLastIdInPlaylist(idInPlaylist: Int)
    fun observeLastIdInPlaylist(): Observable<Int>

    fun getRepeatMode(): Int
    fun setRepeatMode(repeatMode: Int)

    fun getShuffleMode(): Int
    fun setShuffleMode(shuffleMode: Int)

    fun setSkipToPreviousVisibility(visible: Boolean)
    fun observeSkipToPreviousVisibility(): Observable<Boolean>

    fun setSkipToNextVisibility(visible: Boolean)
    fun observeSkipToNextVisibility(): Observable<Boolean>

    fun isMidnightMode() : Observable<Boolean>

    fun getLastMetadata(): LastMetadata
    fun setLastMetadata(metadata: LastMetadata)
    fun observeLastMetadata(): Observable<LastMetadata>

    fun setDefault(): Completable

    fun observeCrossFade(): Observable<Int>
    fun observeGapless(): Observable<Boolean>

    fun observePlaybackSpeed(): Observable<Float>
    fun setPlaybackSpeed(speed: Float)
    fun getPlaybackSpeed(): Float

    fun setLastPositionInQueue(position: Int)
    fun observeLastPositionInQueue(): Observable<Int>
    fun getLastPositionInQueue(): Int

}