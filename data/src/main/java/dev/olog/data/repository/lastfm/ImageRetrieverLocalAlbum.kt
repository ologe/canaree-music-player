package dev.olog.data.repository.lastfm

import dev.olog.core.entity.LastFmAlbum
import dev.olog.core.gateway.base.Id
import dev.olog.data.db.dao.LastFmDao
import dev.olog.data.mapper.toDomain
import dev.olog.data.mapper.toModel
import dev.olog.data.utils.assertBackgroundThread
import javax.inject.Inject

internal class ImageRetrieverLocalAlbum @Inject constructor(
    private val lastFmDao: LastFmDao
) {

    fun mustFetch(albumId: Long): Boolean {
        assertBackgroundThread()
        return lastFmDao.getAlbum(albumId) == null
    }

    fun getCached(id: Id): LastFmAlbum? {
        assertBackgroundThread()
        return lastFmDao.getAlbum(id)?.toDomain()
    }

    fun cache(model: LastFmAlbum) {
        assertBackgroundThread()
        val entity = model.toModel()
        lastFmDao.insertAlbum(entity)
    }

    fun delete(albumId: Long) {
        lastFmDao.deleteAlbum(albumId)
    }

}