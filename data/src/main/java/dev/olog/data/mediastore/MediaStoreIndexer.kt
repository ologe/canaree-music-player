package dev.olog.data.mediastore

import dev.olog.core.AppInitializer
import dev.olog.core.ApplicationScope
import dev.olog.core.schedulers.Schedulers
import dev.olog.data.mediastore.audio.MediaStoreAudioDao
import dev.olog.shared.android.permission.Permission
import dev.olog.shared.android.permission.PermissionManager
import kotlinx.coroutines.launch
import javax.inject.Inject

class MediaStoreIndexer @Inject constructor(
    private val scope: ApplicationScope,
    private val query: MediaStoreQuery,
    private val mediaStoreAudioDao: MediaStoreAudioDao,
    private val permissionManager: PermissionManager,
    private val schedulers: Schedulers,
) : AppInitializer {

    override fun initialize() {
        scope.launch(schedulers.io) {
            permissionManager.awaitPermissions(Permission.Storage)
            launch { initializeSongs() }
        }
    }

    private suspend fun initializeSongs() {
        val songs = query.queryAllAudio()
        mediaStoreAudioDao.replaceAll(songs)
    }

}