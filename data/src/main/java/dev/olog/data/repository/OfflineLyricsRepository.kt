package dev.olog.data.repository

import dev.olog.core.entity.OfflineLyrics
import dev.olog.core.gateway.OfflineLyricsGateway
import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.db.entities.OfflineLyricsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

internal class OfflineLyricsRepository @Inject constructor(
    appDatabase: AppDatabase

) : OfflineLyricsGateway {

    private val dao = appDatabase.offlineLyricsDao()

    override fun observeLyrics(id: Long): Flow<String> {
        return dao.observeLyrics(id)
            .asFlow()
            .map {
                if (it.isEmpty()) ""
                else it[0].lyrics
            }
    }

    override suspend fun saveLyrics(offlineLyrics: OfflineLyrics) {
        return dao.saveLyrics(OfflineLyricsEntity(offlineLyrics.trackId, offlineLyrics.lyrics))
    }
}