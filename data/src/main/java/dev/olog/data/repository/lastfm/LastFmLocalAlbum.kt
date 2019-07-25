package dev.olog.data.repository.lastfm

import android.util.Log
import dev.olog.core.entity.LastFmAlbum
import dev.olog.core.gateway.base.Id
import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.mapper.toDomain
import dev.olog.data.mapper.toModel
import dev.olog.shared.android.utils.assertBackgroundThread
import javax.inject.Inject

internal class LastFmLocalAlbum @Inject constructor(
    appDatabase: AppDatabase

) {

    companion object {
        private val TAG = "D:${LastFmLocalAlbum::class.java.simpleName}"
    }

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
        Log.v(TAG, "cache ${model.id}")
        assertBackgroundThread()
        val entity = model.toModel()
        dao.insertAlbum(entity)
    }

    fun delete(albumId: Long) {
        Log.v(TAG, "delete $albumId")
        dao.deleteAlbum(albumId)
    }

}