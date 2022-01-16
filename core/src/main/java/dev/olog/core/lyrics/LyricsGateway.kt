package dev.olog.core.lyrics

import dev.olog.core.MediaUri
import kotlinx.coroutines.flow.Flow

interface LyricsGateway {

    fun observeLyrics(uri: MediaUri): Flow<String>
    suspend fun saveLyrics(uri: MediaUri, text: String)

    fun getSyncAdjustment(uri: MediaUri): Long
    fun observeSyncAdjustment(uri: MediaUri): Flow<Long>
    suspend fun setSyncAdjustment(uri: MediaUri, millis: Long)

}