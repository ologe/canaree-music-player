package dev.olog.data.repository.lastfm

import dev.olog.core.entity.LastFmTrack
import dev.olog.core.gateway.base.Id
import dev.olog.data.db.lastfm.LastFmDao
import dev.olog.data.mapper.toDomain
import dev.olog.data.mapper.toModel
import javax.inject.Inject

internal class ImageRetrieverLocalTrack @Inject constructor(
    private val lastFmDao: LastFmDao
) {

    fun mustFetch(trackId: Id): Boolean {
        return lastFmDao.getTrack(trackId) == null
    }

    fun getCached(id: Id): LastFmTrack? {
        return lastFmDao.getTrack(id)?.toDomain()
    }

    fun cache(model: LastFmTrack) {
        val entity = model.toModel()
        lastFmDao.insertTrack(entity)
    }

    fun delete(trackId: Long) {
        lastFmDao.deleteTrack(trackId)
    }

}