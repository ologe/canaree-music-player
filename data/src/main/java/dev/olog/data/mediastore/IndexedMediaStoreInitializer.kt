package dev.olog.data.mediastore

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.AppInitializer
import dev.olog.core.ApplicationScope
import dev.olog.core.schedulers.Schedulers
import dev.olog.platform.permission.Permission
import dev.olog.platform.permission.PermissionManager
import kotlinx.coroutines.launch
import javax.inject.Inject

class IndexedMediaStoreInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val app: ApplicationScope,
    private val schedulers: Schedulers,
    private val mediaStoreAudioDao: MediaStoreAudioDao,
    private val mediaStoreQuery: MediaStoreQuery,
    private val permissionManager: PermissionManager,
) : AppInitializer {

    override fun init() {
        app.launch(schedulers.io) {
            permissionManager.awaitPermission(context, Permission.Storage)
            mediaStoreAudioDao.replaceAll(mediaStoreQuery.queryAllAudio())
        }
    }
}