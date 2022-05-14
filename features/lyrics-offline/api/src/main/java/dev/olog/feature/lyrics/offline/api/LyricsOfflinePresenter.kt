package dev.olog.feature.lyrics.offline.api

import kotlinx.coroutines.flow.Flow

interface LyricsOfflinePresenter {

    val currentParagraph: Int

    fun onStart()

    fun onStop()

    fun onStateChanged(position: Int, speed: Float)

    fun getLyrics(): String

    fun observeLyrics(): Flow<Pair<CharSequence, Lyrics>>

    fun updateLyrics(lyrics: String)

    fun updateCurrentTrackId(trackId: Long)

    suspend fun getSyncAdjustment(): String

    fun updateSyncAdjustment(value: Long)

    fun resetTick()

}