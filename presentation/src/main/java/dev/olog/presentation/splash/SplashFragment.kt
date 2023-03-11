package dev.olog.presentation.splash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.presentation.R
import dev.olog.presentation.interfaces.OnPermissionChanged
import dev.olog.platform.extension.alertDialog
import dev.olog.platform.extension.findInContext
import dev.olog.shared.lazyFast
import dev.olog.platform.permission.PermissionManager
import kotlinx.android.synthetic.main.fragment_splash.*
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {

    companion object {
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
        if (!permissionManager.hasMandatoryPermissions()) {
            permissionManager.requestMandatoryPermission(this)
        } else {
            onMandatoryPermissionsGranted()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (permissionManager.isMandatoryPermissionsRequestCode(requestCode)) {
            if (permissionManager.hasMandatoryPermissions()) {
                onMandatoryPermissionsGranted()
            } else {
                onMandatoryPermissionsDenied()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun onMandatoryPermissionsGranted() {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .remove(this)
            .commitAllowingStateLoss()

        (requireActivity().findInContext<OnPermissionChanged>()).onMandatoryPermissionGranted()
    }

    private fun onMandatoryPermissionsDenied() {
        if (permissionManager.hasUserDisabledMandatoryPermissions(this)) {
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