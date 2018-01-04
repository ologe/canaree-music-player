package dev.olog.domain.gateway.prefs

import io.reactivex.Flowable

interface MusicPreferencesGateway {

    fun getBookmark(): Long
    fun setBookmark(bookmark: Long)

    fun getCurrentIdInPlaylist(): Int
    fun setCurrentIdInPlaylist(idInPlaylist: Int)
    fun observeCurrentIdInPlaylist(): Flowable<Int>

    fun getRepeatMode(): Int
    fun setRepeatMode(repeatMode: Int)

    fun getShuffleMode(): Int
    fun setShuffleMode(shuffleMode: Int)

    fun setSkipToPreviousVisibility(visible: Boolean)
    fun observeSkipToPreviousVisibility(): Flowable<Boolean>

    fun setSkipToNextVisibility(visible: Boolean)
    fun observeSkipToNextVisibility(): Flowable<Boolean>

}