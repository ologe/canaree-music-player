package dev.olog.data.repository.lastfm.local

import dev.olog.core.entity.LastFmAlbum
import dev.olog.core.gateway.base.Id
import dev.olog.data.db.dao.LastFmDao
import dev.olog.data.mapper.toDomain
import dev.olog.data.mapper.toModel
import javax.inject.Inject

internal class ImageRetrieverLocalAlbum @Inject constructor(
    private val dao: LastFmDao
) {

    suspend fun mustFetch(albumId: Long): Boolean {
        return dao.getAlbum(albumId) == null
    }

    suspend fun getCached(id: Id): LastFmAlbum? {
        return dao.getAlbum(id)?.toDomain()
    }

    suspend fun cache(model: LastFmAlbum) {
        dao.insertAlbum(model.toModel())
    }

    suspend fun delete(albumId: Long) {
        dao.deleteAlbum(albumId)
    }

}