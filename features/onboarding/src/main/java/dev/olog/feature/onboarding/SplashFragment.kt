package dev.olog.feature.onboarding

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.fragment.app.Fragment
import dev.olog.shared.android.OnPermissionChanged
import dev.olog.shared.android.Permission
import dev.olog.shared.android.Permissions
import dev.olog.shared.android.extensions.alertDialog
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_splash.*

class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val adapter by lazyFast {
        SplashFragmentViewPagerAdapter(childFragmentManager)
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
                requestStoragePermission()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        next.setOnClickListener(null)
    }

    private fun requestStoragePermission() {
        if (!Permissions.canReadStorage(requireContext())) {
            Permissions.requestReadStorage(this)
        } else {
            onStoragePermissionGranted()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (Permissions.checkWriteCode(requestCode)) {
            if (Permissions.canReadStorage(requireContext())) {
                onStoragePermissionGranted()
            } else {
                onStoragePermissionDenied()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun onStoragePermissionGranted() {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .remove(this)
            .commitAllowingStateLoss()

        (requireActivity() as OnPermissionChanged).onPermissionGranted(Permission.STORAGE)
    }

    private fun onStoragePermissionDenied() {
        if (Permissions.hasUserDisabledReadStorage(this)) {
            requireActivity().alertDialog {
                setTitle(R.string.splash_storage_permission)
                setMessage(R.string.splash_storage_permission_disabled)
                setPositiveButton(R.string.popup_positive_ok, { _, _ -> toSettings() })
                setNegativeButton(R.string.popup_negative_no, null)
            }
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