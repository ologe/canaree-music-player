package dev.olog.feature.floating

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.CheckResult
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import javax.inject.Inject

class FeatureFloatingNavigatorImpl @Inject constructor(

) : FeatureFloatingNavigator {

    companion object {
        private const val REQUEST_CODE_HOVER_PERMISSION = 1000
    }

    @SuppressLint("NewApi")
    override fun startService(activity: FragmentActivity) {
        if (hasOverlayPermission(activity)){
            val intent = Intent(activity, FloatingWindowService::class.java)
            ContextCompat.startForegroundService(activity, intent)
        } else {
            val intent = createIntentToRequestOverlayPermission(activity)
            activity.startActivityForResult(intent,
                REQUEST_CODE_HOVER_PERMISSION
            )
        }
    }

    override fun startServiceIfHasPermission(activity: FragmentActivity) {
        if (hasOverlayPermission(activity)){
            val intent = Intent(activity, FloatingWindowService::class.java)
            ContextCompat.startForegroundService(activity, intent)
        }
    }

    override fun handleOnActivityResult(
        activity: FragmentActivity,
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ): Boolean {
        if (requestCode == REQUEST_CODE_HOVER_PERMISSION) {
            startServiceIfHasPermission(activity)
            return true
        }
        return false
    }

    @CheckResult
    private fun hasOverlayPermission(context: Context): Boolean {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Runtime permissions are required. Check for the draw overlay permission.
            Settings.canDrawOverlays(context)
        } else {
            // No runtime permissions required. We're all good.
            true
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @CheckResult
    private fun createIntentToRequestOverlayPermission(context: Context): Intent {
        return Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        )
    }

}