package dev.olog.offlinelyrics

import android.os.Build
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog

@Suppress("DEPRECATION")
fun AlertDialog.enableForService(){
    val windowType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    else
        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
    window?.setType(windowType)
}