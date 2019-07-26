package dev.olog.data.repository

import dev.olog.core.MediaId
import dev.olog.core.gateway.ImageVersionGateway
import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.db.entities.ImageVersion
import javax.inject.Inject

internal class ImageVersionRepository @Inject constructor(
    database: AppDatabase
) : ImageVersionGateway {

    private val dao = database.imageVersionDao()

    override fun getCurrentVersion(mediaId: MediaId): Int {
        var version = dao.getVersion(mediaId.toString())
        if (version == null) {
            version = ImageVersion(mediaId.toString(), 0)
            dao.insertVersion(version)
        }
        return version.version
    }

    override fun increaseCurrentVersion(mediaId: MediaId) {
        getCurrentVersion(mediaId) // populate if empty
        dao.increaseVersion(mediaId.toString())
    }
}