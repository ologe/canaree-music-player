package dev.olog.data.repository.lastfm

import dev.olog.core.entity.LastFmTrack
import dev.olog.data.db.dao.LastFmDao
import dev.olog.data.mapper.toDomain
import dev.olog.data.mapper.toModel
import javax.inject.Inject

internal class ImageRetrieverLocalTrack @Inject constructor(
    private val lastFmDao: LastFmDao
) {

    suspend fun mustFetch(trackId: Long): Boolean {
        return lastFmDao.getTrack(trackId) == null
    }

    suspend fun getCached(id: Long): LastFmTrack? {
        return lastFmDao.getTrack(id)?.toDomain()
    }

    suspend fun cache(model: LastFmTrack) {
        val entity = model.toModel()
        lastFmDao.insertTrack(entity)
    }

    suspend fun delete(trackId: Long) {
        lastFmDao.deleteTrack(trackId)
    }

}