package dev.olog.presentation.main

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.platform.BuildVersion
import dev.olog.platform.permission.Permission
import dev.olog.platform.permission.PermissionManager
import dev.olog.presentation.model.PresentationPreferencesGateway
import javax.inject.Inject

@HiltViewModel
internal class MainActivityViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val presentationPrefs: PresentationPreferencesGateway,
    private val permissionManager: PermissionManager,
    private val playlistGateway: PlaylistGateway,
) : ViewModel() {


    fun isFirstAccess(): Boolean {
        val canReadStorage = permissionManager.hasPermissions(Permission.Storage)
        val isFirstAccess = presentationPrefs.isFirstAccess()
        val isPlaylistLocationSet = isPlaylistLocationSet()
        return !canReadStorage || isFirstAccess || !isPlaylistLocationSet
    }

    private fun isPlaylistLocationSet(): Boolean {
        if (BuildVersion.isQ()) {
            val directory = playlistGateway.getPlaylistDirectory() ?: return false
            return DocumentFile.fromTreeUri(context, directory)?.canWrite() == true
        }
        // not needed pre android Q, mediastore will be used
        return true
    }

}