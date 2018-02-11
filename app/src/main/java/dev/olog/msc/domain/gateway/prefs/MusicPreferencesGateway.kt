package dev.olog.msc.domain.gateway.prefs

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
    fun setMidnightMode(enabled: Boolean)

    fun getLastTitle(): String
    fun setLastTitle(title: String)

    fun getLastSubtitle(): String
    fun setLastSubtitle(subtitle: String)

    fun observeLastMetadata(): Observable<String>

}