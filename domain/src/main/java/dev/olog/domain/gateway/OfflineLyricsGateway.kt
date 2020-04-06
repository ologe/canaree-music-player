package dev.olog.domain.gateway

import dev.olog.domain.entity.OfflineLyrics
import kotlinx.coroutines.flow.Flow

interface OfflineLyricsGateway {

    fun observeLyrics(id: Long): Flow<String>
    suspend fun saveLyrics(offlineLyrics: OfflineLyrics)

    fun getSyncAdjustment(id: Long): Long
    fun observeSyncAdjustment(id: Long): Flow<Long>
    suspend fun setSyncAdjustment(id: Long, millis: Long)

}