package dev.olog.data.repository

import dev.olog.core.entity.OfflineLyrics
import dev.olog.core.gateway.OfflineLyricsGateway
import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.db.entities.LyricsSyncAdjustmentEntity
import dev.olog.data.db.entities.OfflineLyricsEntity
import dev.olog.data.utils.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    override suspend fun setSyncAdjustment(id: Long, millis: Long) {
        syncDao.setSync(LyricsSyncAdjustmentEntity(id, millis))
    }
}