package dev.olog.data.repository.lastfm

import dev.olog.core.entity.LastFmAlbum
import dev.olog.core.gateway.base.Id
import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.mapper.toDomain
import dev.olog.data.mapper.toModel
import dev.olog.shared.utils.assertBackgroundThread
import javax.inject.Inject

internal class LastFmLocalAlbum @Inject constructor(
    appDatabase: AppDatabase

) {

    private val dao = appDatabase.lastFmDao()

    fun mustFetch(albumId: Long): Boolean {
        assertBackgroundThread()
        return dao.getAlbum(albumId) == null
    }

    fun getCached(id: Id): LastFmAlbum? {
        assertBackgroundThread()
        return dao.getAlbum(id)?.toDomain()
    }

    fun cache(model: LastFmAlbum) {
        assertBackgroundThread()
        val entity = model.toModel()
        dao.insertAlbum(entity)
    }

    fun delete(albumId: Long) {
        dao.deleteAlbum(albumId)
    }

}