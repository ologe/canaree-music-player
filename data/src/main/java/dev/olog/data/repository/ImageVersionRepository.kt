package dev.olog.data.repository

import dev.olog.core.MediaId
import dev.olog.core.gateway.ImageVersionGateway
import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.db.entities.ImageVersionEntity
import javax.inject.Inject
import kotlin.math.max

internal class ImageVersionRepository @Inject constructor(
    database: AppDatabase
) : ImageVersionGateway {

    private val dao = database.imageVersionDao()

    private fun getCurrentEntityVersion(mediaId: MediaId): ImageVersionEntity {
        var version = dao.getVersion(mediaId.toString())
        if (version == null) {
            version = ImageVersionEntity(mediaId.toString(), 0, 0)
            dao.insertVersion(version)
        }
        return version
    }

    override fun getCurrentVersion(mediaId: MediaId): Int {
        if (mediaId.isLeaf){
            getCurrentEntityVersion(MediaId.songId(mediaId.leaf!!)).version
        }
        return getCurrentEntityVersion(mediaId).version
    }

    override fun setCurrentVersion(mediaId: MediaId, version: Int) {
        val old = getCurrentEntityVersion(mediaId)
        val new = old.copy(
            version = version,
            maxVersionReached = max(old.maxVersionReached, version)
        )
        dao.insertVersion(new)
    }

    override fun increaseCurrentVersion(mediaId: MediaId) {
        val old = getCurrentEntityVersion(mediaId)
        val newVersion = old.maxVersionReached + 1

        dao.insertVersion(
            old.copy(
                version = newVersion,
                maxVersionReached = newVersion
            )
        )
    }

    override fun deleteAll() {
        dao.deleteAll()
    }
}