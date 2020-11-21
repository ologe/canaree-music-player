package dev.olog.data.local.last.fm

import dev.olog.core.entity.LastFmArtist
import dev.olog.core.gateway.base.Id
import dev.olog.data.local.DateTimeGenerator
import javax.inject.Inject

interface ImageRetrieverLocalArtist {

    suspend fun mustFetch(artistId: Long): Boolean
    suspend fun getCached(id: Id): LastFmArtist?
    suspend fun cache(model: LastFmArtist)
    suspend fun delete(artistId: Long)

}

internal class ImageRetrieverLocalArtistImpl @Inject constructor(
    private val dao: LastFmDao,
    private val dateTimeGenerator: DateTimeGenerator,
) : ImageRetrieverLocalArtist {

    override suspend fun mustFetch(artistId: Long): Boolean {
        return dao.getArtist(artistId) == null
    }

    override suspend fun getCached(id: Id): LastFmArtist? {
        return dao.getArtist(id)?.toDomain()
    }

    override suspend fun cache(model: LastFmArtist) {
        val added = dateTimeGenerator.formattedNow()
        dao.insertArtist(model.toModel(added))
    }

    override suspend fun delete(artistId: Long) {
        dao.deleteArtist(artistId)
    }

}