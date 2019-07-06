package dev.olog.data.repository.lastfm

import dev.olog.core.entity.LastFmArtist
import dev.olog.core.gateway.base.Id
import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.mapper.toDomain
import dev.olog.data.mapper.toModel
import dev.olog.shared.utils.assertBackgroundThread
import javax.inject.Inject

internal class LastFmLocalArtist @Inject constructor(
    appDatabase: AppDatabase

) {

    private val dao = appDatabase.lastFmDao()

    fun mustFetch(artistId: Long): Boolean {
        assertBackgroundThread()
        return dao.getArtist(artistId) == null
    }

    fun getCached(id: Id): LastFmArtist? {
        return dao.getArtist(id)?.toDomain()
    }

    fun cache(model: LastFmArtist) {
        assertBackgroundThread()
        val entity = model.toModel()
        dao.insertArtist(entity)
    }

    fun delete(artistId: Long) {
        assertBackgroundThread()
        dao.deleteArtist(artistId)
    }

}