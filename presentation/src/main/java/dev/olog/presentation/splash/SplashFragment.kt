package dev.olog.presentation.splash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.olog.presentation.R
import dev.olog.presentation.interfaces.OnPermissionChanged
import dev.olog.presentation.interfaces.Permission
import dev.olog.shared.android.Permissions
import dev.olog.shared.android.extensions.alertDialog
import dev.olog.shared.android.extensions.findInContext
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_splash.*

class SplashFragment : Fragment() {

    companion object {
        @JvmStatic
        val TAG = SplashFragment::class.java.name
    }

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

        (requireActivity().findInContext<OnPermissionChanged>()).onPermissionGranted(Permission.STORAGE)
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