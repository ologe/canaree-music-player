package dev.olog.data.repository.lastfm

import dev.olog.core.entity.LastFmArtist
import dev.olog.data.db.dao.LastFmDao
import dev.olog.data.mapper.toDomain
import dev.olog.data.mapper.toModel
import javax.inject.Inject

internal class ImageRetrieverLocalArtist @Inject constructor(
    private val lastFmDao: LastFmDao
) {

    suspend fun mustFetch(artistId: Long): Boolean {
        return lastFmDao.getArtist(artistId) == null
    }

    suspend fun getCached(id: Long): LastFmArtist? {
        return lastFmDao.getArtist(id)?.toDomain()
    }

    suspend fun cache(model: LastFmArtist) {
        val entity = model.toModel()
        lastFmDao.insertArtist(entity)
    }

    suspend fun delete(artistId: Long) {
        lastFmDao.deleteArtist(artistId)
    }

}