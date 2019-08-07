package dev.olog.data.repository.lastfm

import android.util.Log
import dev.olog.core.entity.LastFmArtist
import dev.olog.core.gateway.base.Id
import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.mapper.toDomain
import dev.olog.data.mapper.toModel
import dev.olog.data.utils.assertBackgroundThread
import javax.inject.Inject

internal class ImageRetrieverLocalArtist @Inject constructor(
    appDatabase: AppDatabase

) {

    companion object {
        @JvmStatic
        private val TAG = "D:${ImageRetrieverLocalArtist::class.java.simpleName}"
    }

    private val dao = appDatabase.lastFmDao()

    fun mustFetch(artistId: Long): Boolean {
        assertBackgroundThread()
        return dao.getArtist(artistId) == null
    }

    fun getCached(id: Id): LastFmArtist? {
        return dao.getArtist(id)?.toDomain()
    }

    fun cache(model: LastFmArtist) {
        Log.v(TAG, "cache ${model.id}")
        assertBackgroundThread()
        val entity = model.toModel()
        dao.insertArtist(entity)
    }

    fun delete(artistId: Long) {
        Log.v(TAG, "delete $artistId")
        assertBackgroundThread()
        dao.deleteArtist(artistId)
    }

}