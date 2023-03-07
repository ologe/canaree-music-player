package dev.olog.data.repository.lastfm

import dev.olog.core.entity.LastFmAlbum
import dev.olog.data.db.dao.LastFmDao
import dev.olog.data.mapper.toDomain
import dev.olog.data.mapper.toModel
import javax.inject.Inject

internal class ImageRetrieverLocalAlbum @Inject constructor(
    private val lastFmDao: LastFmDao
) {

    suspend fun mustFetch(albumId: Long): Boolean {
        return lastFmDao.getAlbum(albumId) == null
    }

    suspend fun getCached(id: Long): LastFmAlbum? {
        return lastFmDao.getAlbum(id)?.toDomain()
    }

    suspend fun cache(model: LastFmAlbum) {
        val entity = model.toModel()
        lastFmDao.insertAlbum(entity)
    }

    suspend fun delete(albumId: Long) {
        lastFmDao.deleteAlbum(albumId)
    }

}