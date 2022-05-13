package dev.olog.platform.extension

import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import dev.olog.shared.isOreo

@Suppress("DEPRECATION")
fun AlertDialog.enableForService(){
    val windowType = if (isOreo())
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    else
        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
    window?.setType(windowType)
}