package dev.olog.lib.repository.lastfm

import dev.olog.domain.entity.LastFmAlbum
import dev.olog.lib.db.LastFmDao
import dev.olog.lib.mapper.toDomain
import dev.olog.lib.mapper.toModel
import dev.olog.shared.android.utils.assertBackgroundThread
import timber.log.Timber
import javax.inject.Inject

internal class ImageRetrieverLocalAlbum @Inject constructor(
    private val lastFmDao: LastFmDao
) {

    companion object {
        @JvmStatic
        private val TAG = "D:${ImageRetrieverLocalAlbum::class.java.simpleName}"
    }

    fun mustFetch(albumId: Long): Boolean {
        assertBackgroundThread()
        return lastFmDao.getAlbum(albumId) == null
    }

    fun getCached(id: Long): LastFmAlbum? {
        assertBackgroundThread()
        return lastFmDao.getAlbum(id)?.toDomain()
    }

    fun cache(model: LastFmAlbum) {
        Timber.v("$TAG cache ${model.id}")
        assertBackgroundThread()
        val entity = model.toModel()
        lastFmDao.insertAlbum(entity)
    }

    fun delete(albumId: Long) {
        Timber.v("$TAG delete $albumId")
        assertBackgroundThread()
        lastFmDao.deleteAlbum(albumId)
    }

}