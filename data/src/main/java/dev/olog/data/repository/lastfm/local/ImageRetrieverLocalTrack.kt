package dev.olog.data.repository.lastfm.local

import dev.olog.core.entity.LastFmTrack
import dev.olog.core.gateway.base.Id
import dev.olog.data.db.dao.LastFmDao
import dev.olog.data.mapper.toDomain
import dev.olog.data.mapper.toModel
import javax.inject.Inject

internal class ImageRetrieverLocalTrack @Inject constructor(
    private val dao: LastFmDao
) {

    suspend fun mustFetch(trackId: Id): Boolean {
        return dao.getTrack(trackId) == null
    }

    suspend fun getCached(id: Id): LastFmTrack? {
        return dao.getTrack(id)?.toDomain()
    }

    suspend fun cache(model: LastFmTrack) {
        dao.insertTrack(model.toModel())
    }

    suspend fun delete(trackId: Long) {
        dao.deleteTrack(trackId)
    }

}