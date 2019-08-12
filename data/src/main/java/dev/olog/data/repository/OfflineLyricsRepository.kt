package dev.olog.data.repository

import dev.olog.core.entity.OfflineLyrics
import dev.olog.core.gateway.OfflineLyricsGateway
import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.db.entities.LyricsSyncAdjustmentEntity
import dev.olog.data.db.entities.OfflineLyricsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.reactive.asFlow
import javax.inject.Inject

internal class OfflineLyricsRepository @Inject constructor(
    appDatabase: AppDatabase

) : OfflineLyricsGateway {

    private val lyricsDao = appDatabase.offlineLyricsDao()
    private val syncDao = appDatabase.lyricsSyncAdjustmentDao()

    override fun observeLyrics(id: Long): Flow<String> {
        return lyricsDao.observeLyrics(id)
            .asFlow()
            .map {
                if (it.isEmpty()) ""
                else it[0].lyrics
            }
    }

    override suspend fun saveLyrics(offlineLyrics: OfflineLyrics) {
        return lyricsDao.saveLyrics(OfflineLyricsEntity(offlineLyrics.trackId, offlineLyrics.lyrics))
    }

    override fun getSyncAdjustment(id: Long): Long {
        return syncDao.getSync(id)?.millis ?: 0L
    }

    override fun observeSyncAdjustment(id: Long): Flow<Long> {
        return syncDao.observeSync(id)
            .asFlow()
            .onStart { syncDao.insertSyncIfEmpty(LyricsSyncAdjustmentEntity(id, 0)) }
            .map { it.millis }
    }

    override suspend fun setSyncAdjustment(id: Long, millis: Long) {
        syncDao.setSync(LyricsSyncAdjustmentEntity(id, millis))
    }
}