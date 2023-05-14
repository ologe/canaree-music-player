package dev.olog.presentation.splash

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.platform.BuildVersion
import dev.olog.platform.permission.PermissionManager
import javax.inject.Inject

// TODO allow to change playlist directory change in settings
//   migrate also playlist from one location from another and revoke uri for old uri
@HiltViewModel
class SplashFragmentViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val permissionManager: PermissionManager,
    private val playlistGateway: PlaylistGateway
) : ViewModel() {

    fun isPlaylistDirectorySet(): Boolean {
        if (!BuildVersion.isQ()) {
            return true
        }
        val directoryUri = playlistGateway.getPlaylistDirectory()
        return directoryUri != null && DocumentFile.fromTreeUri(context, directoryUri)?.canWrite() == true
    }

    fun hasMandatoryPermissions(): Boolean {
        return permissionManager.hasMandatoryPermissions()
    }

    fun setPlaylistDirectory(uri: Uri) {
        if (!BuildVersion.isQ()) {
            error("should not be invoked below android Q (api 29)")
        }
        val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        context.contentResolver.takePersistableUriPermission(uri, flags)
        playlistGateway.setPlaylistDirectory(uri)
    }

    fun hasUserDisabledMandatoryPermissions(fragment: Fragment): Boolean {
        return permissionManager.hasUserDisabledMandatoryPermissions(fragment)
    }

}