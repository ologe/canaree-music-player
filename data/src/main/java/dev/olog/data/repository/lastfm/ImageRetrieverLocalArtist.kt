package dev.olog.data.repository.lastfm

import dev.olog.core.entity.LastFmArtist
import dev.olog.core.gateway.base.Id
import dev.olog.data.db.lastfm.LastFmDao
import dev.olog.data.mapper.toDomain
import dev.olog.data.mapper.toModel
import javax.inject.Inject

internal class ImageRetrieverLocalArtist @Inject constructor(
    private val lastFmDao: LastFmDao
) {

    fun mustFetch(artistId: Long): Boolean {
        return lastFmDao.getArtist(artistId) == null
    }

    fun getCached(id: Id): LastFmArtist? {
        return lastFmDao.getArtist(id)?.toDomain()
    }

    fun cache(model: LastFmArtist) {
        val entity = model.toModel()
        lastFmDao.insertArtist(entity)
    }

    fun delete(artistId: Long) {
        lastFmDao.deleteArtist(artistId)
    }

}