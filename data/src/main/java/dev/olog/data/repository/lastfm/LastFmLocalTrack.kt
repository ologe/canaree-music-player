package dev.olog.data.repository.lastfm

import android.util.Log
import dev.olog.core.entity.LastFmTrack
import dev.olog.core.gateway.base.Id
import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.mapper.toDomain
import dev.olog.data.mapper.toModel
import dev.olog.data.utils.assertBackgroundThread
import javax.inject.Inject

internal class LastFmLocalTrack @Inject constructor(
    appDatabase: AppDatabase
) {

    companion object {
        @JvmStatic
        private val TAG = "D:${LastFmLocalTrack::class.java.simpleName}"
    }

    private val dao = appDatabase.lastFmDao()

    fun mustFetch(trackId: Id): Boolean {
        assertBackgroundThread()
        return dao.getTrack(trackId) == null
    }

    fun getCached(id: Id): LastFmTrack? {
        return dao.getTrack(id)?.toDomain()
    }

    fun cache(model: LastFmTrack) {
        Log.v(TAG, "cache ${model.id}")
        assertBackgroundThread()
        val entity = model.toModel()
        dao.insertTrack(entity)
    }

    fun delete(trackId: Long) {
        Log.v(TAG, "delete $trackId")
        assertBackgroundThread()
        dao.deleteTrack(trackId)
    }

}