package dev.olog.feature.splash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.shared.android.extensions.alertDialog
import dev.olog.shared.android.permission.OnPermissionChanged
import dev.olog.shared.android.permission.Permission
import dev.olog.shared.android.permission.PermissionManager
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_splash.*
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {

    companion object {
        @JvmStatic
        val TAG = SplashFragment::class.java.name
    }

    @Inject
    lateinit var permissionManager: PermissionManager

    private val adapter by lazyFast {
        SplashFragmentViewPagerAdapter(
            childFragmentManager
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
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
        if (!permissionManager.hasPermission(Permission.Storage)) {
            permissionManager.requestPermissions(this, Permission.Storage)
        } else {
            onStoragePermissionGranted()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (permissionManager.checkPermissionRequestCode(requestCode)) {
            if (permissionManager.hasPermission(Permission.Storage)) {
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

        (requireActivity() as OnPermissionChanged).onPermissionGranted(Permission.Storage)
    }

    private fun onStoragePermissionDenied() {
        if (permissionManager.hasUserDisabledPermission(this, Permission.Storage)) {
            requireActivity().alertDialog {
                setTitle(localization.R.string.splash_storage_permission)
                setMessage(localization.R.string.splash_storage_permission_disabled)
                setPositiveButton(localization.R.string.popup_positive_ok, { _, _ -> toSettings() })
                setNegativeButton(localization.R.string.popup_negative_no, null)
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