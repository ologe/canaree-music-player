package dev.olog.data.local.lyrics

import dev.olog.core.entity.OfflineLyrics
import dev.olog.core.gateway.OfflineLyricsGateway
import dev.olog.data.local.lyrics.sync.LyricsSyncAdjustmentDao
import dev.olog.data.local.lyrics.sync.LyricsSyncAdjustmentEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

internal class OfflineLyricsRepository @Inject constructor(
    private val offlineLyricsDao: OfflineLyricsDao,
    private val syncDao: LyricsSyncAdjustmentDao

) : OfflineLyricsGateway {

    override fun observeLyrics(id: Long): Flow<String> {
        return offlineLyricsDao.observeLyrics(id)
            .map {
                if (it.isEmpty()) ""
                else it[0].lyrics
            }
    }

    override suspend fun saveLyrics(offlineLyrics: OfflineLyrics) {
        return offlineLyricsDao.saveLyrics(
            OfflineLyricsEntity(
                offlineLyrics.trackId,
                offlineLyrics.lyrics
            )
        )
    }

    override fun getSyncAdjustment(id: Long): Long {
        return syncDao.getSync(id)?.millis ?: 0L
    }

    override fun observeSyncAdjustment(id: Long): Flow<Long> {
        return syncDao.observeSync(id)
            .onStart { syncDao.insertSyncIfEmpty(LyricsSyncAdjustmentEntity(id, 0)) }
            .map { it.millis }
    }

    override suspend fun setSyncAdjustment(id: Long, millis: Long) {
        syncDao.setSync(LyricsSyncAdjustmentEntity(id, millis))
    }
}