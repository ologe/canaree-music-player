package dev.olog.lib.repository.lastfm

import dev.olog.domain.entity.LastFmTrack
import dev.olog.lib.db.LastFmDao
import dev.olog.lib.mapper.toDomain
import dev.olog.lib.mapper.toModel
import dev.olog.shared.android.utils.assertBackgroundThread
import timber.log.Timber
import javax.inject.Inject

internal class ImageRetrieverLocalTrack @Inject constructor(
    private val lastFmDao: LastFmDao
) {

    companion object {
        @JvmStatic
        private val TAG = "D:${ImageRetrieverLocalTrack::class.java.simpleName}"
    }

    fun mustFetch(trackId: Long): Boolean {
        assertBackgroundThread()
        return lastFmDao.getTrack(trackId) == null
    }

    fun getCached(id: Long): LastFmTrack? {
        assertBackgroundThread()
        return lastFmDao.getTrack(id)?.toDomain()
    }

    fun cache(model: LastFmTrack) {
        Timber.v("$TAG cache ${model.id}")
        assertBackgroundThread()
        val entity = model.toModel()
        lastFmDao.insertTrack(entity)
    }

    fun delete(trackId: Long) {
        Timber.v("$TAG delete $trackId")
        assertBackgroundThread()
        lastFmDao.deleteTrack(trackId)
    }

}