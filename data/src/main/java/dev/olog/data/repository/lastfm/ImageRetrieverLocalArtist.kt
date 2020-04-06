package dev.olog.data.repository.lastfm

import dev.olog.domain.entity.LastFmArtist
import dev.olog.data.db.LastFmDao
import dev.olog.data.mapper.toDomain
import dev.olog.data.mapper.toModel
import dev.olog.shared.android.utils.assertBackgroundThread
import timber.log.Timber
import javax.inject.Inject

internal class ImageRetrieverLocalArtist @Inject constructor(
    private val lastFmDao: LastFmDao
) {

    companion object {
        @JvmStatic
        private val TAG = "D:${ImageRetrieverLocalArtist::class.java.simpleName}"
    }

    fun mustFetch(artistId: Long): Boolean {
        assertBackgroundThread()
        return lastFmDao.getArtist(artistId) == null
    }

    fun getCached(id: Long): LastFmArtist? {
        assertBackgroundThread()
        return lastFmDao.getArtist(id)?.toDomain()
    }

    fun cache(model: LastFmArtist) {
        Timber.v("$TAG cache ${model.id}")
        assertBackgroundThread()
        val entity = model.toModel()
        lastFmDao.insertArtist(entity)
    }

    fun delete(artistId: Long) {
        Timber.v("$TAG delete $artistId")
        assertBackgroundThread()
        lastFmDao.deleteArtist(artistId)
    }

}