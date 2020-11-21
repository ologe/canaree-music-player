package dev.olog.data.repository.lastfm.local

import dev.olog.core.entity.LastFmArtist
import dev.olog.core.gateway.base.Id
import dev.olog.data.db.dao.LastFmDao
import dev.olog.data.mapper.toDomain
import dev.olog.data.mapper.toModel
import javax.inject.Inject

internal class ImageRetrieverLocalArtist @Inject constructor(
    private val dao: LastFmDao
) {

    suspend fun mustFetch(artistId: Long): Boolean {
        return dao.getArtist(artistId) == null
    }

    suspend fun getCached(id: Id): LastFmArtist? {
        return dao.getArtist(id)?.toDomain()
    }

    suspend fun cache(model: LastFmArtist) {
        dao.insertArtist(model.toModel())
    }

    suspend fun delete(artistId: Long) {
        dao.deleteArtist(artistId)
    }

}