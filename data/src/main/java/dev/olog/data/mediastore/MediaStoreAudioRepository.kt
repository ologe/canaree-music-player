package dev.olog.data.mediastore

import android.app.Application
import dev.olog.core.AppInitializer
import dev.olog.core.schedulers.Schedulers
import dev.olog.platform.extension.lifecycleScope
import dev.olog.platform.permission.Permission
import dev.olog.platform.permission.PermissionManager
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaStoreAudioRepository @Inject constructor(
    private val application: Application,
    private val preferences: MediaStoreVersionPreferences,
    private val permissionManager: PermissionManager,
    private val mediaStoreQuery: MediaStoreQuery,
    private val dao: MediaStoreAudioInternalDao,
    private val schedulers: Schedulers,
) : AppInitializer {

    // TODO use process lifecycle ON_START/ON_STOP to listen to mediastore changes?
    override fun init() {
        application.lifecycleScope.launch(schedulers.cpu) {
            permissionManager.awaitPermissions(Permission.Storage)
            preferences.onVersionChanged {
                dao.replaceAll(mediaStoreQuery.queryAllAudio())
            }
        }
    }

}