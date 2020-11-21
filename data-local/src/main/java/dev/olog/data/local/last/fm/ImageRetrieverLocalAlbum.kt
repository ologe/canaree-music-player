package dev.olog.data.local.last.fm

import dev.olog.core.entity.LastFmAlbum
import dev.olog.core.gateway.base.Id
import dev.olog.data.local.DateTimeGenerator
import javax.inject.Inject

interface ImageRetrieverLocalAlbum {

    suspend fun mustFetch(albumId: Long): Boolean
    suspend fun getCached(id: Id): LastFmAlbum?
    suspend fun cache(model: LastFmAlbum)
    suspend fun delete(albumId: Long)

}

internal class ImageRetrieverLocalAlbumImpl @Inject constructor(
    private val dao: LastFmDao,
    private val dateTimeGenerator: DateTimeGenerator,
) : ImageRetrieverLocalAlbum {

    override suspend fun mustFetch(albumId: Long): Boolean {
        return dao.getAlbum(albumId) == null
    }

    override suspend fun getCached(id: Id): LastFmAlbum? {
        return dao.getAlbum(id)?.toDomain()
    }

    override suspend fun cache(model: LastFmAlbum) {
        val added = dateTimeGenerator.formattedNow()
        dao.insertAlbum(model.toModel(added))
    }

    override suspend fun delete(albumId: Long) {
        dao.deleteAlbum(albumId)
    }

}