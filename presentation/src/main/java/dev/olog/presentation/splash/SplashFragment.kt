package dev.olog.presentation.splash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.platform.extension.alertDialog
import dev.olog.platform.extension.findInContext
import dev.olog.platform.permission.PermissionManager
import dev.olog.presentation.R
import dev.olog.presentation.interfaces.OnPermissionChanged
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_splash.inkIndicator
import kotlinx.android.synthetic.main.fragment_splash.next
import kotlinx.android.synthetic.main.fragment_splash.viewPager

// request order
// 1. mandatory permissions
// 2. playlist directory (android Q+)
@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    companion object {
        val TAG = SplashFragment::class.java.name
    }

    private val viewModel by viewModels<SplashFragmentViewModel>()

    private val adapter by lazyFast {
        SplashFragmentViewPagerAdapter(
            childFragmentManager
        )
    }

    private val mandatoryPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        if (result.values.any { !it }) {
            // exit, some permissions are not granted
            return@registerForActivityResult
        }
        if (viewModel.isPlaylistDirectorySet()) {
            onMandatoryPermissionsGranted()
        } else {
            playlistDirectoryLauncher.launch(null)
        }
    }

    // TODO show something to explain directory choice
    private val playlistDirectoryLauncher = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri == null) {
            return@registerForActivityResult
        }
        viewModel.setPlaylistDirectory(uri)
        onMandatoryPermissionsGranted()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewPager.adapter = adapter
        inkIndicator.setViewPager(viewPager)
    }

    override fun onResume() {
        super.onResume()
        next.setOnClickListener {
            if (viewPager.currentItem == 0) {
                viewPager.setCurrentItem(1, true)
            } else {
                requestMandatoryPermissions()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        next.setOnClickListener(null)
    }

    private fun requestMandatoryPermissions() {
        if (viewModel.hasUserDisabledMandatoryPermissions(this)) {
            onMandatoryPermissionsDenied()
            return
        }
        if (!viewModel.hasMandatoryPermissions()) {
            mandatoryPermissionLauncher.launch(PermissionManager.MandatoryPermissions)
            return
        }

        if (!viewModel.isPlaylistDirectorySet()) {
            playlistDirectoryLauncher.launch(null)
            return
        }

        onMandatoryPermissionsGranted()
    }

    private fun onMandatoryPermissionsGranted() {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .remove(this)
            .commitAllowingStateLoss()

        (requireActivity().findInContext<OnPermissionChanged>()).onMandatoryPermissionGranted()
    }

    private fun onMandatoryPermissionsDenied() {
        requireActivity().alertDialog {
            setTitle(R.string.splash_storage_permission)
            setMessage(R.string.splash_storage_permission_disabled)
            setPositiveButton(R.string.popup_positive_ok) { _, _ -> toSettings() }
            setNegativeButton(R.string.popup_negative_no, null)
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