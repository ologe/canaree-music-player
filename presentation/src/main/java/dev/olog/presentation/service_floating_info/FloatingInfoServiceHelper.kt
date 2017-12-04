package dev.olog.presentation.service_floating_info

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.annotation.CheckResult
import android.support.annotation.RequiresApi

object FloatingInfoServiceHelper {

    fun startService(context: Context, serviceClass: FloatingInfoServiceBinder){
        val intent = Intent(context, serviceClass.get())
        context.startService(intent)
    }

    @CheckResult
    fun hasOverlayPermission(context: Context): Boolean {

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
    fun createIntentToRequestOverlayPermission(context: Context): Intent {
        return Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context.packageName)
        )
    }

}