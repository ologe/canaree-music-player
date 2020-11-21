package dev.olog.data.local.last.fm

import dev.olog.core.entity.LastFmTrack
import dev.olog.core.gateway.base.Id
import dev.olog.data.local.DateTimeGenerator
import javax.inject.Inject

interface ImageRetrieverLocalTrack {

    suspend fun mustFetch(trackId: Id): Boolean
    suspend fun getCached(id: Id): LastFmTrack?
    suspend fun cache(model: LastFmTrack)
    suspend fun delete(trackId: Long)

}

internal class ImageRetrieverLocalTrackImpl @Inject constructor(
    private val dao: LastFmDao,
    private val dateTimeGenerator: DateTimeGenerator,
) : ImageRetrieverLocalTrack {

    override suspend fun mustFetch(trackId: Id): Boolean {
        return dao.getTrack(trackId) == null
    }

    override suspend fun getCached(id: Id): LastFmTrack? {
        return dao.getTrack(id)?.toDomain()
    }

    override suspend fun cache(model: LastFmTrack) {
        val added = dateTimeGenerator.formattedNow()
        dao.insertTrack(model.toModel(added))
    }

    override suspend fun delete(trackId: Long) {
        dao.deleteTrack(trackId)
    }

}