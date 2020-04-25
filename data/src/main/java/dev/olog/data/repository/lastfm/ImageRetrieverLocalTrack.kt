package dev.olog.data.repository.lastfm

import dev.olog.data.db.LastFmDao
import dev.olog.data.mapper.toDomain
import dev.olog.data.mapper.toModel
import dev.olog.domain.entity.LastFmTrack
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