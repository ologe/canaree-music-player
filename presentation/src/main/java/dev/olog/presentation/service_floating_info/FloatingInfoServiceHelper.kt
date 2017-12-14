package dev.olog.presentation.service_floating_info

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.annotation.CheckResult
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import dev.olog.presentation.utils.isMarshmallow

object FloatingInfoServiceHelper {

    const val REQUEST_CODE_HOVER_PERMISSION = 1000

    @SuppressLint("NewApi")
    fun startService(activity: Activity, serviceClass: FloatingInfoServiceBinder){

        val drawOverlay = FloatingInfoServiceHelper.hasOverlayPermission(activity)
        if (!drawOverlay && isMarshmallow()){
            val intent = FloatingInfoServiceHelper.createIntentToRequestOverlayPermission(activity)
            activity.startActivityForResult(intent, REQUEST_CODE_HOVER_PERMISSION)
        } else {
            val intent = Intent(activity, serviceClass.get())
            ContextCompat.startForegroundService(activity, intent)
        }
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