package dev.olog.feature.splash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import dev.olog.compose.ComposeView
import dev.olog.platform.navigation.FragmentTagFactory
import dev.olog.platform.permission.OnPermissionChanged
import dev.olog.platform.permission.Permission
import dev.olog.platform.permission.PermissionManager
import dev.olog.platform.permission.PermissionResult
import dev.olog.shared.extension.alertDialog
import dev.olog.shared.extension.exhaustive
import dev.olog.shared.extension.findInContext

class SplashFragment : Fragment() {

    companion object {
        val TAG = FragmentTagFactory.create(SplashFragment::class)
    }

    private val permissionHandler = PermissionManager().run {
        requestPermissionHandler(this@SplashFragment, Permission.Storage)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()) {
            SplashScreen(
                onRequestPermission = {
                    requestStoragePermission()
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        permissionHandler.dispose()
    }

    private suspend fun requestStoragePermission() {
        val result = permissionHandler.request()
        when (result) {
            PermissionResult.Granted -> onStoragePermissionGranted()
            PermissionResult.Denied -> {}
            PermissionResult.RequestRationale -> onStoragePermissionDenied()
        }.exhaustive
    }

    private fun onStoragePermissionGranted() {
        requireActivity().supportFragmentManager.commit(true) {
            remove(this@SplashFragment)
        }

        (requireActivity().findInContext<OnPermissionChanged>()).onPermissionGranted(Permission.Storage)
    }

    private fun onStoragePermissionDenied() {
        requireActivity().alertDialog {
            setTitle(localization.R.string.splash_storage_permission)
            setMessage(localization.R.string.splash_storage_permission_disabled)
            setPositiveButton(localization.R.string.popup_positive_ok) { _, _ -> toSettings() }
            setNegativeButton(localization.R.string.popup_negative_no, null)
        }
    }

    private fun toSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", requireContext().packageName, null)
        )
        startActivity(intent)
    }

}