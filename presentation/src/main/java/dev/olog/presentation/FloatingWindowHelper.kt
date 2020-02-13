package dev.olog.presentation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.CheckResult
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import dev.olog.intents.Classes

object FloatingWindowHelper {

    const val REQUEST_CODE_HOVER_PERMISSION = 1000

    @SuppressLint("NewApi")
    fun startServiceOrRequestOverlayPermission(activity: Activity){
        if (hasOverlayPermission(activity)){
            val intent = Intent(activity, Class.forName(Classes.SERVICE_FLOATING))
            ContextCompat.startForegroundService(activity, intent)
        } else {
            val intent = createIntentToRequestOverlayPermission(activity)
            activity.startActivityForResult(intent,
                REQUEST_CODE_HOVER_PERMISSION
            )
        }
    }

    @SuppressLint("NewApi")
    fun startServiceIfHasOverlayPermission(activity: Activity){

        if (hasOverlayPermission(activity)){
            val intent = Intent(activity, Class.forName(Classes.SERVICE_FLOATING))
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