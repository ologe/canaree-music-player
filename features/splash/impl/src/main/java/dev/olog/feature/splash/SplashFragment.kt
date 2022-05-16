package dev.olog.feature.splash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import dev.olog.feature.splash.databinding.FragmentSplashBinding
import dev.olog.platform.navigation.FragmentTagFactory
import dev.olog.platform.permission.OnPermissionChanged
import dev.olog.platform.permission.Permission
import dev.olog.platform.permission.PermissionManager
import dev.olog.platform.permission.PermissionResult
import dev.olog.platform.viewBinding
import dev.olog.shared.autoDisposeJob
import dev.olog.shared.extension.alertDialog
import dev.olog.shared.extension.exhaustive
import dev.olog.shared.extension.findInContext
import dev.olog.shared.extension.launchWhenResumed
import dev.olog.shared.extension.lazyFast

class SplashFragment : Fragment(R.layout.fragment_splash) {

    companion object {
        val TAG = FragmentTagFactory.create(SplashFragment::class)
    }

    private val binding by viewBinding(FragmentSplashBinding::bind)
    private var permissionJob by autoDisposeJob()

    private val adapter by lazyFast {
        SplashFragmentViewPagerAdapter(childFragmentManager)
    }

    private val permissionHandler = PermissionManager().run {
        requestPermissionHandler(this@SplashFragment, Permission.Storage)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        viewPager.adapter = adapter
        inkIndicator.setViewPager(viewPager)

        next.setOnClickListener {
            if (viewPager.currentItem == 0) {
                viewPager.setCurrentItem(1, true)
            } else {
                permissionJob = launchWhenResumed {
                    requestStoragePermission()
                }
            }
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